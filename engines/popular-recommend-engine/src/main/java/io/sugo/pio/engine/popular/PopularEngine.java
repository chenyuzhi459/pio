package io.sugo.pio.engine.popular;

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
public class PopularEngine extends Engine<PopularTrainingData, PopularPreparaData, PopularModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    public PopularEngine(PropertyHose propertyHose,
                         BatchEventHose batchEventHose,
                         Repository repository) {
        super(null);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }

    @Override
    public DataSource<PopularTrainingData> createDatasource(Params params) {
        return new PopularDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<PopularTrainingData, PopularPreparaData> createPreparator(Params params) {
        return new PopularPreparator();
    }

    @Override
    public Algorithm<PopularPreparaData, PopularModelData> createAlgorithm(Params params) {
        return new PopularAlgorithm();
    }

    @Override
    public Model<PopularModelData> createModel() {
        return new PopularModel(repository);
    }


}
