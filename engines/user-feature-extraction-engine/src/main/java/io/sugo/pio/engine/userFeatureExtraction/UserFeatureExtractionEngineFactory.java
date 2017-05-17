package io.sugo.pio.engine.userFeatureExtraction;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineFactory;
import io.sugo.pio.engine.training.EngineParams;

/**
 * Created by penghuan on 2017/4/7.
 */
public class UserFeatureExtractionEngineFactory implements EngineFactory {

    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final EngineParams engineParams;

    public UserFeatureExtractionEngineFactory(
            PropertyHose propertyHose,
            BatchEventHose batchEventHose,
            Repository repository,
            EngineParams engineParams
    ) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }

    @Override
    public Engine createEngine() {
        return new UserFeatureExtractionEngine(propertyHose, batchEventHose, repository, engineParams);
    }
}
