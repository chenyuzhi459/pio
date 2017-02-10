package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.als.ALSEngineFactory;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class ALSTraining extends AbstractTraining {
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(ALSResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        Repository repository = new LocalFileRepository(ALSResource.REPOSITORY_PATH);
        ALSEngineFactory engineFactory = new ALSEngineFactory(eventHose, repository);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
