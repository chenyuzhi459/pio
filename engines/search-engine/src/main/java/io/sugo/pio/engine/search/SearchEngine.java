package io.sugo.pio.engine.search;

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
import io.sugo.pio.engine.search.eval.SearchEvalActualResult;
import io.sugo.pio.engine.search.eval.SearchEvalIndicators;
import io.sugo.pio.engine.search.eval.SearchEvalQuery;
import io.sugo.pio.engine.training.*;

/**
 */
public class SearchEngine extends Engine<SearchTrainingData, SearchPreparaData, SearchModelData, SearchEvalQuery, SearchEvalActualResult, SearchEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final SearchEngineParams searchEngineParams;

    public SearchEngine(PropertyHose propertyHose,
                        BatchEventHose batchEventHose,
                        Repository repository,
                        SearchEngineParams searchEngineParams) {
        super(searchEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.searchEngineParams = searchEngineParams;
    }

    @Override
    public DataSource<SearchTrainingData, SearchEvalQuery, SearchEvalActualResult> createDatasource(Params params) {
        return new SearchDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<SearchTrainingData, SearchPreparaData> createPreparator(Params params) {
        return new SearchPreparator();
    }

    @Override
    public Algorithm<SearchPreparaData, SearchModelData> createAlgorithm(Params params) {
        return new SearchAlgorithm();
    }

    @Override
    public Model<SearchModelData> createModel() {
        return new SearchModel(repository);
    }

    @Override
    protected Evalution<SearchModelData, SearchEvalQuery, SearchEvalActualResult, SearchEvalIndicators> createEval() {
        return null;
    }

}
