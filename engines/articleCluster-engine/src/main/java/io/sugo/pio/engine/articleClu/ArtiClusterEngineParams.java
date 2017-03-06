package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.articleClu.params.ArtiDatasourceParams;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 */
public class ArtiClusterEngineParams implements EngineParams {
    private final int sampleNum;
    private final String[] labels;

    @JsonCreator
    public ArtiClusterEngineParams(@JsonProperty("sampleNum") int sampleNum,
                                   @JsonProperty("labels") String[] labels){
        this.sampleNum = sampleNum;
        this.labels = labels;
    }

    @Override
    public ArtiDatasourceParams getDatasourceParams() {
        return new ArtiDatasourceParams(sampleNum, labels);
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
