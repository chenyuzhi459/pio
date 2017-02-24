package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.articleClu.ArtiClusterEngineFactory;
import io.sugo.pio.engine.articleClu.ArtiClusterEngineParams;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.HtcBatchEventHose;
import io.sugo.pio.engine.demo.data.MmwJdbcHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineFactory;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class ArtiClusterTraining extends AbstractTraining {
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        String timeColumn = "1";
        String url = "jdbc:postgresql://192.168.0.210:5432/mmw";
        String table = "mmq";
        String username = "postgres";
        String password = "123456";
        int count = 100000;
        int par = 1;
        String[] pNames ={"parlabel","sublabel", "title", "content"};

        FileUtil.deleteDirectory(new File(TextSimilarResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTC_DATA_PATH, Constants.HTC_DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        MmwJdbcHose mmwJdbcEventHose = new MmwJdbcHose(timeColumn, url, table, username, password, count, par, pNames);
        Repository repository = new LocalFileRepository(TextSimilarResource.REPOSITORY_PATH);
        ArtiClusterEngineParams artiClusterEngineParams = new ArtiClusterEngineParams();
        EngineFactory engineFactory = new ArtiClusterEngineFactory(propHose, mmwJdbcEventHose, repository, artiClusterEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
