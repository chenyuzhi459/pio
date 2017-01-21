package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.fp.FpEngineFactory;
import io.sugo.pio.engine.fp.data.FpModelData;
import io.sugo.pio.engine.fp.data.FpPreparaData;
import io.sugo.pio.engine.fp.data.FpTrainingData;
import io.sugo.pio.engine.training.*;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class FpTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(FpResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        EngineFactory<FpTrainingData, FpPreparaData, FpModelData> engineFactory = new FpEngineFactory(propHose, eventHose);
        Preparator<FpTrainingData, FpPreparaData> preparator = engineFactory.createPreparator();
        DataSource<FpTrainingData> dataSource = engineFactory.createDatasource();
        FpTrainingData trainingData = dataSource.readTraining(sc);
        FpPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<FpPreparaData, FpModelData> alg = engineFactory.createAlgorithm();
        FpModelData modelData = alg.train(sc, preparedData);
        Model<FpModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(FpResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}


