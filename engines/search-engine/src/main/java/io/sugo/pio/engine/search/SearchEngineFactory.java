package io.sugo.pio.engine.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.search.data.SearchModelData;
import io.sugo.pio.engine.search.data.SearchPreparaData;
import io.sugo.pio.engine.search.data.SearchTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class SearchEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final SearchEngineParams searchEngineParams;

    @JsonCreator
    public SearchEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                @JsonProperty("repository") Repository repository,
                                @JsonProperty("engineParams") SearchEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.searchEngineParams = engineParams;
    }

    @Override
    public Engine createEngine() {
        return new SearchEngine(propertyHose, batchEventHose, repository, searchEngineParams);
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
    public SearchEngineParams getSearchEngineParams() {
        return searchEngineParams;
    }
}
