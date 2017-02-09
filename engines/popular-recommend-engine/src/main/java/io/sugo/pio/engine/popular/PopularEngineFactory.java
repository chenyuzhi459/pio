package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.popular.data.PopularModelData;
import io.sugo.pio.engine.popular.data.PopularPreparaData;
import io.sugo.pio.engine.popular.data.PopularTrainingData;
import io.sugo.pio.engine.popular.engine.PopularAlgorithm;
import io.sugo.pio.engine.popular.engine.PopularDatasource;
import io.sugo.pio.engine.popular.engine.PopularModel;
import io.sugo.pio.engine.popular.engine.PopularPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class PopularEngineFactory implements EngineFactory<PopularTrainingData, PopularPreparaData, PopularModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    @JsonCreator
    public PopularEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                           @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                           @JsonProperty("repository") Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }

    @Override
    public DataSource<PopularTrainingData> createDatasource() {
        return new PopularDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<PopularTrainingData, PopularPreparaData> createPreparator() {
        return new PopularPreparator();
    }

    @Override
    public Algorithm<PopularPreparaData, PopularModelData> createAlgorithm() {
        return new PopularAlgorithm();
    }

    @Override
    public Model<PopularModelData> createModel() {
        return new PopularModel(repository);
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
