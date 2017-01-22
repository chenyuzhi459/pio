package io.sugo.pio.engine.search;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.search.data.SearchModelData;
import io.sugo.pio.engine.search.data.SearchPreparaData;
import io.sugo.pio.engine.search.data.SearchTrainingData;
import io.sugo.pio.engine.search.engine.SearchAlgorithm;
import io.sugo.pio.engine.search.engine.SearchDatasource;
import io.sugo.pio.engine.search.engine.SearchModel;
import io.sugo.pio.engine.search.engine.SearchPreparator;
import io.sugo.pio.engine.search.param.SearchDatasourceParams;
import io.sugo.pio.engine.training.*;

/**
 */
public class SearchEngineFactory implements EngineFactory<SearchTrainingData, SearchPreparaData, SearchModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;

    public SearchEngineFactory(PropertyHose propertyHose,
                                BatchEventHose batchEventHose) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
    }

    @Override
    public DataSource<SearchTrainingData> createDatasource() {
        return new SearchDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<SearchTrainingData, SearchPreparaData> createPreparator() {
        return new SearchPreparator();
    }

    @Override
    public Algorithm<SearchPreparaData, SearchModelData> createAlgorithm() {
        return new SearchAlgorithm();
    }

    @Override
    public Model<SearchModelData> createModel() {
        return new SearchModel();
    }
}
