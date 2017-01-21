package io.sugo.pio.engine.popular;

import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;

/**
 */
public class PopularModelFactory implements ModelFactory<PopResult> {
    @Override
    public PredictionModel<PopResult> loadModel(Repository repository) {
        return null;
    }
}
