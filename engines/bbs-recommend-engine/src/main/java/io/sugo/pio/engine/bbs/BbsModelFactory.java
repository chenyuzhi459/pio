package io.sugo.pio.engine.bbs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.bbs.engine.*;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.hadoop.fs.Path;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 */
public class BbsModelFactory implements ModelFactory<BbsResult> {
    final Repository repository;

    @JsonCreator
    public BbsModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<BbsResult> loadModel() {
        try{
            return new BbsPredictionModel(repository);
        }catch (IOException e){
            throw new RuntimeException(e);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class BbsPredictionModel implements PredictionModel<BbsResult>{
        private final SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("bbs");
        private final SparkContext sc = new SparkContext(sparkConf);
        private final ToAnalysis toAnalysis;
        private final LSAQueryEngine lsaQueryEngine;

        BbsPredictionModel(Repository repository) throws IOException, ClassNotFoundException {
            this.toAnalysis = new ToAnalysis();
            this.lsaQueryEngine = deserialize(repository);
        }

        public LSAQueryEngine deserialize(Repository repository) throws IOException, ClassNotFoundException {
            String path = repository.getPath();
            String wholePath = new Path(path, Constants.FILE_NAME()).toUri().toString();
            FileInputStream bis = new FileInputStream(wholePath);
            ObjectInputStream ois = new ObjectInputStream(bis);
            LSAQueryEngine lsaQueryEngine = (LSAQueryEngine)ois.readObject();
            return lsaQueryEngine;
        }

        @Override
        public BbsResult predict(PredictionQueryObject query) {
            try{
                String title = ((BbsQuery) query).getTitle();
                String content = ((BbsQuery) query).getContent();
                String allcontent = title + " " +content;
                AnsjpartBbs ansjpartBbs = new AnsjpartBbs();
                String[] words = ansjpartBbs.text2words(allcontent, toAnalysis);
                BbsPredict bbsPredictObj = new BbsPredict(lsaQueryEngine, words);
                Tuple2<String [], String[]> res = bbsPredictObj.predict(sc);
                return new BbsResult(res._1(), res._2());
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}