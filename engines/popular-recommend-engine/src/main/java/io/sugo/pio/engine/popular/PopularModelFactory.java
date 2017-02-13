package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PopularModelFactory implements ModelFactory<PopResult> {
    private final Repository repository;

    @JsonCreator
    public PopularModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<PopResult> loadModel() {
        try {
            return new PopularPredictionModel(new QueryableModelData(repository));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class PopularPredictionModel implements PredictionModel<PopResult> {
        private final QueryableModelData queryableModelData;

        PopularPredictionModel(QueryableModelData queryableModelData) {
            this.queryableModelData = queryableModelData;
        }

        public PopResult predict(PredictionQueryObject query) {
            try {
                PopQuery popQuery = (PopQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (popQuery.getTags() != null) {
                    map.put(Constants.CATEGORY(), popQuery.getTags());
                }

                int queryNum = 10;
                if (popQuery.getNum() != null) {
                    String Num = popQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.INT, true), queryNum, null);
                List<String> items = res.get(Constants.ITEM_ID());
                return new PopResult(items);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
