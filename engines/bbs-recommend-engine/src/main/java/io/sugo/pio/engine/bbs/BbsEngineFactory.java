package io.sugo.pio.engine.bbs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class BbsEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final Repository repository;
    private final BatchEventHose batchEventHoseItem;
    private final BbsEngineParams engineParams;


    @JsonCreator
    public BbsEngineFactory(@JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                            @JsonProperty("batchEventHoseItem") BatchEventHose batchEventHoseItem,
                            @JsonProperty("repository") Repository repository,
                            @JsonProperty("engineParams") BbsEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.repository = repository;
        this.batchEventHoseItem = batchEventHoseItem;
        this.engineParams = engineParams;
    }
    @Override
    public Engine createEngine() {
        return new BbsEngine(batchEventHose, batchEventHoseItem,repository, engineParams);
    }

    @JsonProperty
    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

}
