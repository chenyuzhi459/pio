package io.sugo.pio.engine.bbs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.bbs.params.BbsDatasourceParams;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 */
public class BbsEngineParams implements EngineParams {
    private final int sampleNum;
    private final String[] labels;

    @JsonCreator
    public BbsEngineParams(@JsonProperty("sampleNum") int sampleNum,
                           @JsonProperty("labels") String[] labels){
        this.sampleNum = sampleNum;
        this.labels = labels;
    }

    @Override
    public BbsDatasourceParams getDatasourceParams() {
        return new BbsDatasourceParams(sampleNum, labels);
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
