package io.sugo.pio.engine.userExtension;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userExtension.data.UserExtensionModelData;
import io.sugo.pio.engine.userExtension.data.UserExtensionPreparaData;
import io.sugo.pio.engine.userExtension.data.UserExtensionTrainingData;
import io.sugo.pio.engine.userExtension.engine.UserExtensionAlgorithm;
import io.sugo.pio.engine.userExtension.engine.UserExtensionDatasource;
import io.sugo.pio.engine.userExtension.engine.UserExtensionModel;
import io.sugo.pio.engine.userExtension.engine.UserExtensionPreparator;
import io.sugo.pio.engine.userExtension.eval.UserExtensionEvalActualResult;
import io.sugo.pio.engine.userExtension.eval.UserExtensionEvalIndicators;
import io.sugo.pio.engine.userExtension.eval.UserExtensionEvalQuery;

/**
 */
public class UserExtensionEngine extends Engine<UserExtensionTrainingData, UserExtensionPreparaData, UserExtensionModelData, UserExtensionEvalQuery, UserExtensionEvalActualResult, UserExtensionEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final EngineParams engineParams;

    public UserExtensionEngine(PropertyHose propertyHose,
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
    public DataSource<UserExtensionTrainingData, UserExtensionEvalQuery, UserExtensionEvalActualResult> createDatasource(Params params) {
        return new UserExtensionDatasource(propertyHose, batchEventHose, repository);
    }

    @Override
    public Preparator<UserExtensionTrainingData, UserExtensionPreparaData> createPreparator(Params params) {
        return new UserExtensionPreparator();
    }

    @Override
    public Algorithm<UserExtensionPreparaData, UserExtensionModelData> createAlgorithm(Params params) {
        return new UserExtensionAlgorithm();
    }

    @Override
    public Model<UserExtensionModelData> createModel() {
        return new UserExtensionModel(repository);
    }

    @Override
    protected Evalution createEval() {
        return null;
    }

}
