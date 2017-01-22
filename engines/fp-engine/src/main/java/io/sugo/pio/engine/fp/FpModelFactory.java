package io.sugo.pio.engine.fp;

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
public class FpModelFactory implements ModelFactory<FpResult> {
    @Override
    public PredictionModel<FpResult> loadModel(Repository repository) {
        try{
            return new FpPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    static class FpPredictionModel implements PredictionModel<FpResult>{
        private final QueryableModelData queryableModelData;
        FpPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public FpResult predict(PredictionQueryObject query) {
            try {
                FpQuery fpQuery = (FpQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (fpQuery.getItem_id() != null) {
                    map.put(Constants.ITEMID(), fpQuery.getItem_id());
                }

                int queryNum = 10;
                if (fpQuery.getNum() != null) {
                    String Num = fpQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.CONSEQUENTS());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.INT, true), queryNum, null);
                List<String> items = res.get(Constants.CONSEQUENTS());
                return new FpResult(items);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
