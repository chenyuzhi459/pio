package io.sugo.pio.engine.textSimilar;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import io.sugo.pio.engine.textSimilar.engine.Constants;
import io.sugo.pio.engine.textSimilar.engine.LucenceConstants;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class TextSimilarModelFactory implements ModelFactory<TextSimilarResult> {
    @Override
    public PredictionModel<TextSimilarResult> loadModel(Repository repository) {
        try{
            return new TextSimilarPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    static class TextSimilarPredictionModel implements PredictionModel<TextSimilarResult>{
        private final QueryableModelData queryableModelData;
        TextSimilarPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public TextSimilarResult predict(PredictionQueryObject query) {
            try {
                TextSimilarQuery TextSimilarQuery = (TextSimilarQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (TextSimilarQuery.getItem_id() != null) {
                    map.put(Constants.ITEM_ID(), TextSimilarQuery.getItem_id());
                }
                int queryNum = 10;
                if (TextSimilarQuery.getNum() != null) {
                    String Num = TextSimilarQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }
                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.RELATED_ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.LONG, true), queryNum,null);
                List<String> simItemIds = res.get(Constants.RELATED_ITEM_ID());
                return new TextSimilarResult(simItemIds);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}