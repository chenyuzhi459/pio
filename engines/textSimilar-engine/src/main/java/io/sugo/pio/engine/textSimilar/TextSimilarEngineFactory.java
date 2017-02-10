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
public class TextSimilarEngineFactory implements EngineFactory<TextSimilarTrainingData, TextSimilarPreparaData, TextSimilarModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    @JsonCreator
    public TextSimilarEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                           @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                           @JsonProperty("repository") Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }
    @Override
    public Engine createEngine() {
        return new TextSimilarEngine(propertyHose, batchEventHose, repository);
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
}
