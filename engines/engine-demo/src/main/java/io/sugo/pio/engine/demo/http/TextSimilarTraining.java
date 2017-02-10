package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.HtcBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.textSimilar.TextSimilarEngineFactory;
import io.sugo.pio.engine.textSimilar.TextSimilarEngineParams;
import io.sugo.pio.engine.textSimilar.data.TextSimilarModelData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarPreparaData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarTrainingData;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.demo.FileUtil;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class TextSimilarTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(TextSimilarResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTC_DATA_PATH, Constants.HTC_DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(TextSimilarResource.REPOSITORY_PATH);
        TextSimilarEngineParams textSimilarEngineParams = new TextSimilarEngineParams();
        EngineFactory<TextSimilarTrainingData, TextSimilarPreparaData, TextSimilarModelData> engineFactory = new TextSimilarEngineFactory(propHose, eventHose, repository, textSimilarEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
