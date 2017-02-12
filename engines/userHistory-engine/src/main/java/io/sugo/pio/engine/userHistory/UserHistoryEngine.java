package io.sugo.pio.engine.userHistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userHistory.data.UserHistoryModelData;
import io.sugo.pio.engine.userHistory.data.UserHistoryPreparaData;
import io.sugo.pio.engine.userHistory.data.UserHistoryTrainingData;
import io.sugo.pio.engine.userHistory.engine.UserHistoryAlgorithm;
import io.sugo.pio.engine.userHistory.engine.UserHistoryDatasource;
import io.sugo.pio.engine.userHistory.engine.UserHistoryModel;
import io.sugo.pio.engine.userHistory.engine.UserHistoryPreparator;
import io.sugo.pio.engine.userHistory.eval.UserHistoryEvalActualResult;
import io.sugo.pio.engine.userHistory.eval.UserHistoryEvalIndicators;
import io.sugo.pio.engine.userHistory.eval.UserHistoryEvalQuery;

/**
 */
public class UserHistoryEngine extends Engine<UserHistoryTrainingData, UserHistoryPreparaData, UserHistoryModelData, UserHistoryEvalQuery, UserHistoryEvalActualResult, UserHistoryEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final UserHistoryEngineParams userHistoryEngineParams;


    public UserHistoryEngine(PropertyHose propertyHose,
                             BatchEventHose batchEventHose,
                             Repository repository,
                             UserHistoryEngineParams userHistoryEngineParams) {
        super(userHistoryEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.userHistoryEngineParams = userHistoryEngineParams;
    }

    @Override
    public DataSource<UserHistoryTrainingData, UserHistoryEvalQuery, UserHistoryEvalActualResult> createDatasource(Params params) {
        return new UserHistoryDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<UserHistoryTrainingData, UserHistoryPreparaData> createPreparator(Params params) {
        return new UserHistoryPreparator();
    }

    @Override
    public Algorithm<UserHistoryPreparaData, UserHistoryModelData> createAlgorithm(Params params) {
        return new UserHistoryAlgorithm();
    }

    @Override
    public Model<UserHistoryModelData> createModel() {
        return new UserHistoryModel(repository);
    }

    @Override
    protected Evalution<UserHistoryModelData, UserHistoryEvalQuery, UserHistoryEvalActualResult, UserHistoryEvalIndicators> createEval() {
        return null;
    }

}
