package io.sugo.pio.engine.als;

import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;

/**
 */
public class ALSModelFactory implements ModelFactory<ALSQuery, ALSResult> {
    @Override
    public PredictionModel<ALSQuery, ALSResult> loadModel(Repository repository) {
        return null;
    }
}
