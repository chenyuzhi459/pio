package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.popular.data.PopularModelData;
import io.sugo.pio.engine.popular.data.PopularPreparaData;
import io.sugo.pio.engine.popular.data.PopularTrainingData;
import io.sugo.pio.engine.popular.engine.PopularEngineFactory;
import io.sugo.pio.spark.engine.*;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.input.PropertyHose;
import io.sugo.pio.spark.engine.data.output.LocalFileRepository;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class PopularTraining extends AbstractTraining {
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(PopluarResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        EngineFactory<PopularTrainingData, PopularPreparaData, PopularModelData, QueryableModelData> engineFactory = new PopularEngineFactory(propHose, eventHose);
        Preparator<PopularTrainingData, PopularPreparaData> preparator = engineFactory.createPreparator();
        DataSource<PopularTrainingData> dataSource = engineFactory.createDatasource();
        PopularTrainingData trainingData = dataSource.readTraining(sc);
        PopularPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<PopularPreparaData, PopularModelData> alg = engineFactory.createAlgorithm();
        PopularModelData modelData = alg.train(sc, preparedData);
        Model<PopularModelData, QueryableModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(PopluarResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
