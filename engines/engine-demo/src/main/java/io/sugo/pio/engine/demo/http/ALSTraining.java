package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.als.ALSEngineFactory;
import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.spark.engine.Algorithm;
import io.sugo.pio.spark.engine.DataSource;
import io.sugo.pio.spark.engine.Model;
import io.sugo.pio.spark.engine.Preparator;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.input.PropertyHose;
import io.sugo.pio.spark.engine.data.output.LocalFileRepository;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class ALSTraining extends AbstractTraining {
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(ALSResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        ALSEngineFactory engineFactory = new ALSEngineFactory(propHose, eventHose);
        Preparator<ALSTrainingData, ALSPreparedData> preparator = engineFactory.createPreparator();
        DataSource<ALSTrainingData> dataSource = engineFactory.createDatasource();
        ALSTrainingData trainingData = dataSource.readTraining(sc);
        ALSPreparedData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<ALSPreparedData, ALSModelData> alg = engineFactory.createAlgorithm();
        ALSModelData modelData = alg.train(sc, preparedData);
        Model<ALSModelData, QueryableModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(ALSResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
