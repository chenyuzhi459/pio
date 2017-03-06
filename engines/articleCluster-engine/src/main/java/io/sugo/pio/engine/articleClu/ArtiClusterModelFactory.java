package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.articleClu.engine.Ansjpart;
import io.sugo.pio.engine.data.output.FSInputStream;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import io.sugo.pio.engine.articleClu.engine.Constants;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.apache.spark.mllib.linalg.Vector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class ArtiClusterModelFactory implements ModelFactory<ArtiClusterResult> {
    final Repository clf_repository;
    final Repository w2v_repository;
    final Repository map_repository;

    @JsonCreator
    public ArtiClusterModelFactory(@JsonProperty("repository") Repository clf_repository,
                                   @JsonProperty("repository") Repository w2v_repository,
                                   @JsonProperty("repository") Repository map_repository) {
        this.clf_repository = clf_repository;
        this.w2v_repository = w2v_repository;
        this.map_repository = map_repository;
    }

    @Override
    public PredictionModel<ArtiClusterResult> loadModel() {
        try{
            return new ArtiClusterPredictionModel(clf_repository, w2v_repository, map_repository);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @JsonProperty
    public Repository getClf_repository() {
        return clf_repository;
    }

    @JsonProperty
    public Repository getW2v_repository() {
        return clf_repository;
    }

    @JsonProperty
    public Repository getMap_repository() {
        return clf_repository;
    }

    static class ArtiClusterPredictionModel implements PredictionModel<ArtiClusterResult>{
        private final SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("predict");
        private final SparkContext sc = new SparkContext(sparkConf);
        private final Map<String, String> labelMap;
        private final ToAnalysis toAnalysis;
        private final Word2VecModel word2VecModel;
        private final LogisticRegressionModel logisticRegressionModel;

        ArtiClusterPredictionModel(Repository clf_repository, Repository w2v_repository, Repository map_repository) throws IOException {
            this.labelMap = readLabelMap(map_repository);
            this.word2VecModel = Word2VecModel.load(sc, w2v_repository.getPath());
            this.logisticRegressionModel = LogisticRegressionModel.load(sc, clf_repository.getPath());
            this.toAnalysis = new ToAnalysis();
        }

        public Map<String, String> readLabelMap(Repository repository) throws IOException {
            FSInputStream fs = repository.openInput(Constants.LABEL_FILE());
            BufferedReader bufr=new BufferedReader(new InputStreamReader(fs));
            String line;
            Map<String, String> labelMap = new LinkedHashMap<>();
            while ((line = bufr.readLine())!= null){
                String[] labels = line.split(Constants.LABEL_SEP());
                String actLabel = labels[0];
                String quantLabel = labels[1];
                labelMap.put(quantLabel, actLabel);
            }
            return labelMap;
        }

        @Override
        public ArtiClusterResult predict(PredictionQueryObject query) {
            try {
                String title = ((ArtiClusterQuery) query).getTitle();
                String content = ((ArtiClusterQuery) query).getContent();
                String allcontent = title + " " +content;
                Ansjpart ansjpart = new Ansjpart();
                Vector textVec = ansjpart.text2vec(allcontent, word2VecModel, toAnalysis);
                if (textVec != null){
                    String quantLabel = String.valueOf((int)logisticRegressionModel.predict(textVec));
                    String predictLabel = labelMap.get(quantLabel);
                    return new ArtiClusterResult(predictLabel);
                }
                else {
                    return null;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}