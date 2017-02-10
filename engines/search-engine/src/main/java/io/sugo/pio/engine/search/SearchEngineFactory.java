package io.sugo.pio.engine.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.search.data.SearchModelData;
import io.sugo.pio.engine.search.data.SearchPreparaData;
import io.sugo.pio.engine.search.data.SearchTrainingData;
import io.sugo.pio.engine.search.engine.SearchAlgorithm;
import io.sugo.pio.engine.search.engine.SearchDatasource;
import io.sugo.pio.engine.search.engine.SearchModel;
import io.sugo.pio.engine.search.engine.SearchPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class SearchEngineFactory implements EngineFactory<SearchTrainingData, SearchPreparaData, SearchModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    @JsonCreator
    public SearchEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                @JsonProperty("repository") Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }

    @Override
    public Engine createEngine() {
        return new SearchEngine(propertyHose, batchEventHose, repository);
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
