package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.flow.FlowEngineParams;
import io.sugo.pio.engine.flow.FlowEngineFactory;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineFactory;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class FlowTraining extends AbstractTraining {
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(FlowResource.PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(FlowResource.PATH);
        FlowEngineParams popEngineParams = new FlowEngineParams();
        EngineFactory engineFactory = new FlowEngineFactory(propHose, eventHose, repository, popEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
