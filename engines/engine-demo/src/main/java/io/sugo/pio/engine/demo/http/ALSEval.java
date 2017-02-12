package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.als.ALSEngineFactory;
import io.sugo.pio.engine.als.ALSEngineParams;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.training.Engine;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class ALSEval extends AbstractEval{
    @Override
    protected void doEval(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(ALSResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        Repository repository = new LocalFileRepository(ALSResource.REPOSITORY_PATH);
        ALSEngineParams alsEngineParams = new ALSEngineParams(5,10);
        ALSEngineFactory engineFactory = new ALSEngineFactory(eventHose, repository, alsEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.eval(sc);
    }
}