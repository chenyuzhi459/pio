package io.sugo.pio.engine.detail;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.*;

/**
 */
public class DetailModelFactory implements ModelFactory<DetailResult>{
    @Override
    public PredictionModel<DetailResult> loadModel(Repository repository) {
        try {
            return new DetailPredictionModel(new QueryableModelData(repository));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class DetailPredictionModel implements PredictionModel<DetailResult> {
        private final QueryableModelData queryableModelData;

        DetailPredictionModel(QueryableModelData queryableModelData) {
            this.queryableModelData = queryableModelData;
        }

        @Override
        public DetailResult predict(PredictionQueryObject query) {
            try {
                DetailQuery detailQuery = (DetailQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (detailQuery.getItem_id() != null) {
                    map.put(Constants.ITEM_ID(), detailQuery.getItem_id());
                }

                int queryNum = 10;
                if (detailQuery.getNum() != null) {
                    String Num = detailQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.RELATED_ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.FLOAT, true), queryNum, null);
                List<String> items = res.get(Constants.RELATED_ITEM_ID());
                return new DetailResult(items);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
