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
import io.sugo.pio.engine.popular.eval.PopularEvalActualResult;
import io.sugo.pio.engine.popular.eval.PopularEvalIndicators;
import io.sugo.pio.engine.popular.eval.PopularEvalQuery;
import io.sugo.pio.engine.training.*;

/**
 */
public class PopularEngine extends Engine<PopularTrainingData, PopularPreparaData, PopularModelData, PopularEvalQuery, PopularEvalActualResult, PopularEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final PopEngineParams popEngineParams;

    public PopularEngine(PropertyHose propertyHose,
                         BatchEventHose batchEventHose,
                         Repository repository,
                         PopEngineParams popEngineParams) {
        super(popEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.popEngineParams = popEngineParams;
    }

    @Override
    public DataSource<PopularTrainingData, PopularEvalQuery, PopularEvalActualResult> createDatasource(Params params) {
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

    @Override
    protected Evalution createEval() {
        return null;
    }

}
