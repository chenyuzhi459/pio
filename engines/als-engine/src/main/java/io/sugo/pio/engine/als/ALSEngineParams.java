package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 */
public class ALSEngineParams implements EngineParams {
    private final int rank;

    @JsonCreator
    public ALSEngineParams(@JsonProperty("rank") int rank) {
        this.rank = rank;
    }

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

    @JsonProperty
    public int getRank() {
        return rank;
    }
}
