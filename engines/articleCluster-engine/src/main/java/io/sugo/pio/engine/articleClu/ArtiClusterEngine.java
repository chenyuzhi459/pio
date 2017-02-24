package io.sugo.pio.engine.articleClu;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.articleClu.data.ArtiClusterModelData;
import io.sugo.pio.engine.articleClu.data.ArtiClusterPreparaData;
import io.sugo.pio.engine.articleClu.data.ArtiClusterTrainingData;
import io.sugo.pio.engine.articleClu.engine.ArtiClusterAlgorithm;
import io.sugo.pio.engine.articleClu.engine.ArtiClusterDatasource;
import io.sugo.pio.engine.articleClu.engine.ArtiClusterModel;
import io.sugo.pio.engine.articleClu.engine.ArtiClusterPreparator;
import io.sugo.pio.engine.articleClu.eval.ArtiClusterEvalActualResult;
import io.sugo.pio.engine.articleClu.eval.ArtiClusterEvalIndicators;
import io.sugo.pio.engine.articleClu.eval.ArtiClusterEvalQuery;
import io.sugo.pio.engine.training.*;

/**
 */
public class ArtiClusterEngine extends Engine<ArtiClusterTrainingData, ArtiClusterPreparaData, ArtiClusterModelData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult, ArtiClusterEvalIndicators>{
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final ArtiClusterEngineParams textSimilarEngineParams;

    public ArtiClusterEngine(PropertyHose propertyHose,
                             BatchEventHose batchEventHose,
                             Repository repository,
                             ArtiClusterEngineParams textSimilarEngineParams) {
        super(textSimilarEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.textSimilarEngineParams = textSimilarEngineParams;
    }

    @Override
    protected DataSource<ArtiClusterTrainingData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult> createDatasource(Params params) {
        return new ArtiClusterDatasource(propertyHose, batchEventHose);
    }

    @Override
    protected Preparator<ArtiClusterTrainingData, ArtiClusterPreparaData> createPreparator(Params params) {
        return new ArtiClusterPreparator();
    }

    @Override
    protected Algorithm<ArtiClusterPreparaData, ArtiClusterModelData> createAlgorithm(Params params) {
        return new ArtiClusterAlgorithm();
    }

    @Override
    protected Model<ArtiClusterModelData> createModel() {
        return new ArtiClusterModel(repository);
    }

    @Override
    protected Evalution<ArtiClusterModelData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult, ArtiClusterEvalIndicators> createEval() {
        return null;
    }
}
