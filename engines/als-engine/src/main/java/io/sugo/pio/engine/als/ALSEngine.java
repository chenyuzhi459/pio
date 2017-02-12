package io.sugo.pio.engine.als;

import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.als.engine.*;
import io.sugo.pio.engine.als.eval.ALSEvalActualResult;
import io.sugo.pio.engine.als.eval.ALSEvalIndicators;
import io.sugo.pio.engine.als.eval.ALSEvalQuery;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class ALSEngine extends Engine<ALSTrainingData, ALSPreparedData, ALSModelData, ALSEvalQuery, ALSEvalActualResult, ALSEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final Repository repository;
    private final ALSEngineParams alsEngineParams;

    public ALSEngine(BatchEventHose batchEventHose,
                     Repository repository,
                     ALSEngineParams alsEngineParams) {
        super(alsEngineParams);
        this.batchEventHose = batchEventHose;
        this.repository = repository;
        this.alsEngineParams = alsEngineParams;
    }
    @Override
    protected DataSource<ALSTrainingData, ALSEvalQuery, ALSEvalActualResult> createDatasource(Params params) {
        return new ALSDataSource(batchEventHose);
    }

    @Override
    protected Preparator<ALSTrainingData, ALSPreparedData> createPreparator(Params params) {
        return new ALSPreparator();
    }

    @Override
    protected Algorithm<ALSPreparedData, ALSModelData> createAlgorithm(Params params) {
        return new ALSAlgorithm(alsEngineParams.getAlgorithmParams());
    }

    @Override
    protected Model<ALSModelData> createModel() {
        return new ALSDModel(repository);
    }

    @Override
    protected Evalution<ALSModelData, ALSEvalQuery, ALSEvalActualResult, ALSEvalIndicators> createEval() {
        return new ALSEvalution();
    }
}
