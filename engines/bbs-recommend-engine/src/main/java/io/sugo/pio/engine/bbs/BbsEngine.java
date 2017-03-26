package io.sugo.pio.engine.bbs;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.bbs.data.BbsModelData;
import io.sugo.pio.engine.bbs.data.BbsPreparaData;
import io.sugo.pio.engine.bbs.data.BbsTrainingData;
import io.sugo.pio.engine.bbs.engine.BbsAlgorithm;
import io.sugo.pio.engine.bbs.engine.BbsDatasource;
import io.sugo.pio.engine.bbs.engine.BbsModel;
import io.sugo.pio.engine.bbs.engine.BbsPreparator;
import io.sugo.pio.engine.bbs.eval.BbsEvalActualResult;
import io.sugo.pio.engine.bbs.eval.BbsEvalIndicators;
import io.sugo.pio.engine.bbs.eval.BbsEvalQuery;
import io.sugo.pio.engine.training.*;

/**
 */
public class BbsEngine extends Engine<BbsTrainingData, BbsPreparaData, BbsModelData, BbsEvalQuery, BbsEvalActualResult, BbsEvalIndicators>{
    private final BatchEventHose batchEventHose;
    private final Repository repository;
    private final BatchEventHose batchEventHoseItem;
    private final BbsEngineParams bbsEngineParams;

    public BbsEngine(BatchEventHose batchEventHose,
                     BatchEventHose batchEventHoseItem,
                             Repository repository,
                     BbsEngineParams bbsEngineParams
                     ) {
        super(bbsEngineParams);
        this.batchEventHose = batchEventHose;
        this.batchEventHoseItem = batchEventHoseItem;
        this.repository = repository;
        this.bbsEngineParams = bbsEngineParams;
    }

    @Override
    protected DataSource<BbsTrainingData, BbsEvalQuery, BbsEvalActualResult> createDatasource(Params params) {
        return new BbsDatasource(batchEventHose, batchEventHoseItem, bbsEngineParams.getDatasourceParams());
    }

    @Override
    protected Preparator<BbsTrainingData, BbsPreparaData> createPreparator(Params params) {
        return new BbsPreparator();
    }

    @Override
    protected Algorithm<BbsPreparaData, BbsModelData> createAlgorithm(Params params) {
        return new BbsAlgorithm();
    }

    @Override
    protected Model<BbsModelData> createModel() {
        return new BbsModel(repository);
    }

    @Override
    protected Evalution<BbsModelData, BbsEvalQuery, BbsEvalActualResult, BbsEvalIndicators> createEval() {
        return null;
    }
}
