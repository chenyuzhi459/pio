package io.sugo.pio.engine.userHistory;

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
public class UserHistoryModelFactory implements ModelFactory<UserHistoryResult> {
    @Override
    public PredictionModel<UserHistoryResult> loadModel(Repository repository) {
        try{
            return new UserHistoryPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    static class UserHistoryPredictionModel implements PredictionModel<UserHistoryResult>{
        private final QueryableModelData queryableModelData;
        UserHistoryPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public UserHistoryResult predict(PredictionQueryObject query) {
            try {
                UserHistoryQuery userHistoryQuery = (UserHistoryQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (userHistoryQuery.getUser_id() != null) {
                    map.put(Constants.USER_ID(), userHistoryQuery.getUser_id());
                }
                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.LONG, true), 30,null);
                List<String> hisItemIds = res.get(Constants.ITEM_ID());
                return new UserHistoryResult(hisItemIds);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
