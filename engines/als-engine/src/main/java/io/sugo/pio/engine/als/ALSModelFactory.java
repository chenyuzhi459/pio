package io.sugo.pio.engine.als;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import org.apache.lucene.search.SortField;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ALSModelFactory implements ModelFactory<ALSResult> {
    @Override
    public PredictionModel<ALSResult> loadModel(Repository repository) {
        try{
            return new ALSPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    static class ALSPredictionModel implements PredictionModel<ALSResult>{
        private final QueryableModelData queryableModelData;
        ALSPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public ALSResult predict(PredictionQueryObject query) {
            try {
                ALSQuery alsQuery = (ALSQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (alsQuery.getUser_id() != null) {
                    map.put(Constants.USER_ID(), alsQuery.getUser_id());
                }

                int queryNum = 10;
                if (alsQuery.getNum() != null) {
                    String Num = alsQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.FLOAT, true), queryNum,null);
                List<String> items = res.get(Constants.ITEM_ID());
                return new ALSResult(items);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
