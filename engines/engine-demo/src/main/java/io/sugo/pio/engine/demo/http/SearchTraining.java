package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.search.SearchEngineFactory;
import io.sugo.pio.engine.search.data.SearchModelData;
import io.sugo.pio.engine.search.data.SearchPreparaData;
import io.sugo.pio.engine.search.data.SearchTrainingData;
import io.sugo.pio.engine.training.*;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class SearchTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(SearchResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        Repository repository = new LocalFileRepository(SearchResource.REPOSITORY_PATH);
        EngineFactory<SearchTrainingData, SearchPreparaData, SearchModelData> engineFactory = new SearchEngineFactory(propHose, eventHose, repository);
        Preparator<SearchTrainingData, SearchPreparaData> preparator = engineFactory.createPreparator();
        DataSource<SearchTrainingData> dataSource = engineFactory.createDatasource();
        SearchTrainingData trainingData = dataSource.readTraining(sc);
        SearchPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<SearchPreparaData, SearchModelData> alg = engineFactory.createAlgorithm();
        SearchModelData modelData = alg.train(sc, preparedData);
        Model<SearchModelData> model = engineFactory.createModel();

        model.save(modelData);
    }
}
