package io.sugo.pio.engine.userFeatureExtraction;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionModelData;
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionPrepareData;
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionTrainingData;
import io.sugo.pio.engine.userFeatureExtraction.engine.UserFeatureExtractionAlgorithm;
import io.sugo.pio.engine.userFeatureExtraction.engine.UserFeatureExtractionDatasource;
import io.sugo.pio.engine.userFeatureExtraction.engine.UserFeatureExtractionModel;
import io.sugo.pio.engine.userFeatureExtraction.engine.UserFeatureExtractionPreparator;
import io.sugo.pio.engine.userFeatureExtraction.eval.UserFeatureExtractionEvalActualResult;
import io.sugo.pio.engine.userFeatureExtraction.eval.UserFeatureExtractionEvalIndicators;
import io.sugo.pio.engine.userFeatureExtraction.eval.UserFeatureExtractionEvalQuery;

/**
 * Created by penghuan on 2017/4/7.
 */
public class UserFeatureExtractionEngine extends Engine<UserFeatureExtractionTrainingData, UserFeatureExtractionPrepareData, UserFeatureExtractionModelData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalIndicators> {

    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final EngineParams engineParams;

    public UserFeatureExtractionEngine(
            PropertyHose propertyHose,
            BatchEventHose batchEventHose,
            Repository repository,
            EngineParams engineParams) {
        super(engineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }

    @Override
    protected DataSource<UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult> createDatasource(Params params) {
        return new UserFeatureExtractionDatasource(propertyHose, batchEventHose);
    }

    @Override
    protected Preparator<UserFeatureExtractionTrainingData, UserFeatureExtractionPrepareData> createPreparator(Params params) {
        return new UserFeatureExtractionPreparator();
    }

    @Override
    protected Algorithm<UserFeatureExtractionPrepareData, UserFeatureExtractionModelData> createAlgorithm(Params params) {
        return new UserFeatureExtractionAlgorithm();
    }

    @Override
    protected Model<UserFeatureExtractionModelData> createModel() {
        return new UserFeatureExtractionModel(repository);
    }

    @Override
    protected Evalution<UserFeatureExtractionModelData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalIndicators> createEval() {
        return null;
    }
}
