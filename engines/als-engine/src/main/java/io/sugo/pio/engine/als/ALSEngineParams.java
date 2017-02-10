package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.als.params.ALSAlgParams;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 */
public class ALSEngineParams implements EngineParams {
    private final int rank;
    private final int numIter;

    @JsonCreator
    public ALSEngineParams(@JsonProperty("rank") int rank,
                            @JsonProperty("numIter") int numIter) {
        this.rank = rank;
        this.numIter = numIter;
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
    public ALSAlgParams getAlgorithmParams() {
        return new ALSAlgParams(rank, numIter);
    }

    @JsonProperty
    public int getRank() {
        return rank;
    }

    @JsonProperty
    public int getNumIter() {
        return numIter;
    }

}
