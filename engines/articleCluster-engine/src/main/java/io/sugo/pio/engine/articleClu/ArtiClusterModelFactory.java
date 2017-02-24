package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import io.sugo.pio.engine.articleClu.engine.Constants;
import io.sugo.pio.engine.articleClu.engine.LucenceConstants;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ArtiClusterModelFactory implements ModelFactory<ArtiClusterResult> {
    private final Repository repository;

    @JsonCreator
    public ArtiClusterModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<ArtiClusterResult> loadModel() {
        try{
            return new ArtiClusterPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class ArtiClusterPredictionModel implements PredictionModel<ArtiClusterResult>{
        private final QueryableModelData queryableModelData;
        ArtiClusterPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public ArtiClusterResult predict(PredictionQueryObject query) {
            try {
                ArtiClusterQuery artiClusterQuery = (ArtiClusterQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (artiClusterQuery.getItem_id() != null) {
                    map.put(Constants.ITEM_ID(), artiClusterQuery.getItem_id());
                }
                int queryNum = 10;
                if (artiClusterQuery.getNum() != null) {
                    String Num = artiClusterQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }
                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.RELATED_ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.LONG, true), queryNum,null);
                List<String> simItemIds = res.get(Constants.RELATED_ITEM_ID());
                return new ArtiClusterResult(simItemIds);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}