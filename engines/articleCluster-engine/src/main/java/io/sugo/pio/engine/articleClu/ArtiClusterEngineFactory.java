package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class ArtiClusterEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository clf_repository;
    private final Repository w2v_repository;
    private final Repository map_repository;
    private final ArtiClusterEngineParams engineParams;

    @JsonCreator
    public ArtiClusterEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                    @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                    @JsonProperty("clf_repository") Repository clf_repository,
                                    @JsonProperty("w2v_repository") Repository w2v_repository,
                                    @JsonProperty("map_repository") Repository map_repository,
                                    @JsonProperty("engineParams") ArtiClusterEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.clf_repository = clf_repository;
        this.w2v_repository = w2v_repository;
        this.map_repository = map_repository;
        this.engineParams = engineParams;
    }
    @Override
    public Engine createEngine() {
        return new ArtiClusterEngine(propertyHose, batchEventHose, clf_repository, w2v_repository, map_repository, engineParams);
    }

    @JsonProperty
    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    @JsonProperty
    public PropertyHose getPropertyHose() {
        return propertyHose;
    }

    @JsonProperty
    public Repository getClf_repository() {
        return clf_repository;
    }

    @JsonProperty
    public Repository getW2v_repository() {
        return w2v_repository;
    }

    @JsonProperty
    public Repository getMap_repository() {
        return map_repository;
    }

    @JsonProperty
    public ArtiClusterEngineParams getEngineParams() {
        return engineParams;
    }
}
