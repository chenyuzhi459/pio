package io.sugo.pio.engine.articleClu;

import io.sugo.pio.engine.articleClu.params.ArtiDatasourceParams;
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
    private final Repository clf_repository;
    private final Repository w2v_repository;
    private final Repository map_repository;
    private final ArtiClusterEngineParams artiClusterEngineParams;

    public ArtiClusterEngine(PropertyHose propertyHose,
                             BatchEventHose batchEventHose,
                             Repository clf_repository,
                             Repository w2v_repository,
                             Repository map_repository,
                             ArtiClusterEngineParams artiClusterEngineParams) {
        super(artiClusterEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.clf_repository = clf_repository;
        this.w2v_repository = w2v_repository;
        this.map_repository = map_repository;
        this.artiClusterEngineParams = artiClusterEngineParams;
    }

    @Override
    protected DataSource<ArtiClusterTrainingData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult> createDatasource(Params params) {
        return new ArtiClusterDatasource(propertyHose, batchEventHose, artiClusterEngineParams.getDatasourceParams());
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
        return new ArtiClusterModel(clf_repository, w2v_repository, map_repository);
    }

    @Override
    protected Evalution<ArtiClusterModelData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult, ArtiClusterEvalIndicators> createEval() {
        return null;
    }
}
