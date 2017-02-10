package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.fp.FpEngineFactory;
import io.sugo.pio.engine.fp.FpEngineParams;
import io.sugo.pio.engine.fp.data.FpModelData;
import io.sugo.pio.engine.fp.data.FpPreparaData;
import io.sugo.pio.engine.fp.data.FpTrainingData;
import io.sugo.pio.engine.training.*;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class FpTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(FpResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(FpResource.REPOSITORY_PATH);
        FpEngineParams fpEngineParams = new FpEngineParams();
        EngineFactory<FpTrainingData, FpPreparaData, FpModelData> engineFactory = new FpEngineFactory(propHose, eventHose, repository, fpEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}


