package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.HtcBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.search.SearchEngineFactory;
import io.sugo.pio.engine.search.data.SearchModelData;
import io.sugo.pio.engine.search.data.SearchPreparaData;
import io.sugo.pio.engine.search.data.SearchTrainingData;
import io.sugo.pio.engine.search.param.SearchDatasourceParams;
import io.sugo.pio.engine.training.*;
import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.execution.vectorized.ColumnVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 */
public class SearchTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(SearchResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTCDATA_PATH, Constants.HTC_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        EngineFactory<SearchTrainingData, SearchPreparaData, SearchModelData> engineFactory = new SearchEngineFactory(propHose, eventHose);
        Preparator<SearchTrainingData, SearchPreparaData> preparator = engineFactory.createPreparator();
        DataSource<SearchTrainingData> dataSource = engineFactory.createDatasource();
        SearchTrainingData trainingData = dataSource.readTraining(sc);
        SearchPreparaData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<SearchPreparaData, SearchModelData> alg = engineFactory.createAlgorithm();
        SearchModelData modelData = alg.train(sc, preparedData);
        Model<SearchModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(SearchResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
