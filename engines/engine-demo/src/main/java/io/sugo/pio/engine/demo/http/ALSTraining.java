package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.als.ALSEngineFactory;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.training.Algorithm;
import io.sugo.pio.engine.training.DataSource;
import io.sugo.pio.engine.training.Model;
import io.sugo.pio.engine.training.Preparator;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
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
        ALSEngineFactory engineFactory = new ALSEngineFactory(eventHose);
        Preparator<ALSTrainingData, ALSPreparedData> preparator = engineFactory.createPreparator();
        DataSource<ALSTrainingData> dataSource = engineFactory.createDatasource();
        ALSTrainingData trainingData = dataSource.readTraining(sc);
        ALSPreparedData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<ALSPreparedData, ALSModelData> alg = engineFactory.createAlgorithm();
        ALSModelData modelData = alg.train(sc, preparedData);
        Model<ALSModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(ALSResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
