package io.sugo.pio.engine.detail;

import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;

/**
 */
public class DetailModelFactory implements ModelFactory<DetailQuery, DetailResult>{
    @Override
    public PredictionModel<DetailQuery, DetailResult> loadModel(Repository repository) {
        return null;
    }
}
