package io.sugo.pio.engine.userHistory;

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

/**
 */
public class UserHistoryEngineFactory implements EngineFactory<UserHistoryTrainingData, UserHistoryPreparaData, UserHistoryModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    public UserHistoryEngineFactory(PropertyHose propertyHose,
                                    BatchEventHose batchEventHose,
                                    Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }

    @Override
    public DataSource<UserHistoryTrainingData> createDatasource() {
        return new UserHistoryDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<UserHistoryTrainingData, UserHistoryPreparaData> createPreparator() {
        return new UserHistoryPreparator();
    }

    @Override
    public Algorithm<UserHistoryPreparaData, UserHistoryModelData> createAlgorithm() {
        return new UserHistoryAlgorithm();
    }

    @Override
    public Model<UserHistoryModelData> createModel() {
        return new UserHistoryModel(repository);
    }
}