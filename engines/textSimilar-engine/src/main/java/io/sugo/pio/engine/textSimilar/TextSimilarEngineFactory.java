package io.sugo.pio.engine.textSimilar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.textSimilar.data.TextSimilarModelData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarPreparaData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class TextSimilarEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final TextSimilarEngineParams textSimilarEngineParams;

    @JsonCreator
    public TextSimilarEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                    @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                    @JsonProperty("repository") Repository repository,
                                    @JsonProperty("engineParams") TextSimilarEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.textSimilarEngineParams = engineParams;
    }
    @Override
    public Engine createEngine() {
        return new TextSimilarEngine(propertyHose, batchEventHose, repository, textSimilarEngineParams);
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
    public Repository getRepository() {
        return repository;
    }

    @JsonProperty
    public TextSimilarEngineParams getTextSimilarEngineParams() {
        return textSimilarEngineParams;
    }
}
