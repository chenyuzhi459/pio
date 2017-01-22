package io.sugo.pio.engine.detail;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

import java.io.IOException;

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
            return null;
        }
    }
}
