package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userHistory.UserHistoryEngineFactory;
import io.sugo.pio.engine.userHistory.UserHistoryEngineParams;
import io.sugo.pio.engine.userHistory.data.UserHistoryModelData;
import io.sugo.pio.engine.userHistory.data.UserHistoryPreparaData;
import io.sugo.pio.engine.userHistory.data.UserHistoryTrainingData;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class UserHistoryTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(UserHistoryResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(UserHistoryResource.REPOSITORY_PATH);
        UserHistoryEngineParams userHistoryEngineParams = new UserHistoryEngineParams();
        EngineFactory<UserHistoryTrainingData, UserHistoryPreparaData, UserHistoryModelData> engineFactory = new UserHistoryEngineFactory(propHose, eventHose, repository, userHistoryEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
