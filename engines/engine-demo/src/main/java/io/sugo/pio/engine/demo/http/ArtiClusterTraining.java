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
import java.util.List;

/**
 */
public class ArtiClusterTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        String timeColumn = "1";
        String url = "jdbc:postgresql://192.168.0.210:5432/mmw";
        String table = "mmq";
        String username = "postgres";
        String password = "123456";
        int count = 80000;
        int par = 1;
        String[] pNames ={"parlabel","sublabel", "title", "content"};

        int sampleNum = 4000;
        String[] labels = {"准备怀孕圈", "难孕难育圈", "孕8-10月圈", "生男生女圈", "宝宝取名圈", "宝宝营养辅食", "宝宝常见病圈", "早教幼教圈"};
        FileUtil.deleteDirectory(new File(ArtiClusterResource.CLF_PATH));
        FileUtil.deleteDirectory(new File(ArtiClusterResource.W2V_PATH));
        FileUtil.deleteDirectory(new File(ArtiClusterResource.MAP_PATH));
        BatchEventHose eventHose = new HtcBatchEventHose(Constants.HTC_DATA_PATH, Constants.HTC_DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        MmwJdbcHose mmwJdbcEventHose = new MmwJdbcHose(timeColumn, url, table, username, password, count, par, pNames);
        Repository clf_repository = new LocalFileRepository(ArtiClusterResource.CLF_PATH);
        Repository w2v_repository = new LocalFileRepository(ArtiClusterResource.W2V_PATH);
        Repository map_repository = new LocalFileRepository(ArtiClusterResource.MAP_PATH);
        ArtiClusterEngineParams artiClusterEngineParams = new ArtiClusterEngineParams(sampleNum, labels);
        EngineFactory engineFactory = new ArtiClusterEngineFactory(propHose, mmwJdbcEventHose, clf_repository, w2v_repository, map_repository, artiClusterEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
