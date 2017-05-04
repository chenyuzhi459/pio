package io.sugo.pio.engine.userFeatureExtraction;

import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 * Created by penghuan on 2017/4/7.
 */
public class UserFeatureExtractionEngineParams implements EngineParams {
    @Override
    public Params getDatasourceParams() {
        return null;
    }

    @Override
    public Params getPreparatorParams() {
        return null;
    }

    @Override
    public Params getAlgorithmParams() {
        return null;
    }
}
