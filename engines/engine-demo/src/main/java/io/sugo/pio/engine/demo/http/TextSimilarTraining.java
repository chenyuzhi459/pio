package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.HtcBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.textSimilar.TextSimilarEngineFactory;
import io.sugo.pio.engine.textSimilar.data.TextSimilarModelData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarPreparaData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarTrainingData;
import io.sugo.pio.engine.training.*;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class TextSimilarTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {

        FileUtils.deleteDirectory(new File(TextSimilarResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTC_DATA_PATH, Constants.HTC_DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(TextSimilarResource.REPOSITORY_PATH);
        EngineFactory<TextSimilarTrainingData, TextSimilarPreparaData, TextSimilarModelData> engineFactory = new TextSimilarEngineFactory(propHose, eventHose, repository);
        Preparator<TextSimilarTrainingData, TextSimilarPreparaData> preparator = engineFactory.createPreparator();
        DataSource<TextSimilarTrainingData> dataSource = engineFactory.createDatasource();
        TextSimilarTrainingData trainingData = dataSource.readTraining(sc);
        TextSimilarPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<TextSimilarPreparaData, TextSimilarModelData> alg = engineFactory.createAlgorithm();
        TextSimilarModelData modelData = alg.train(sc, preparedData);
        Model<TextSimilarModelData> model = engineFactory.createModel();
        model.save(modelData);
    }
}
