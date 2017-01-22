package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.HtcBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userHistory.UserHistoryEngineFactory;
import io.sugo.pio.engine.userHistory.data.UserHistoryModelData;
import io.sugo.pio.engine.userHistory.data.UserHistoryPreparaData;
import io.sugo.pio.engine.userHistory.data.UserHistoryTrainingData;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.execution.vectorized.ColumnVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 */
public class UserHistoryTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(UserHistoryResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTCDATA_PATH, Constants.HTC_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        EngineFactory<UserHistoryTrainingData, UserHistoryPreparaData, UserHistoryModelData> engineFactory = new UserHistoryEngineFactory(propHose, eventHose);
        Preparator<UserHistoryTrainingData, UserHistoryPreparaData> preparator = engineFactory.createPreparator();
        DataSource<UserHistoryTrainingData> dataSource = engineFactory.createDatasource();
        UserHistoryTrainingData trainingData = dataSource.readTraining(sc);
        UserHistoryPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<UserHistoryPreparaData, UserHistoryModelData> alg = engineFactory.createAlgorithm();
        UserHistoryModelData modelData = alg.train(sc, preparedData);
        Model<UserHistoryModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(UserHistoryResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
