package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.bbs.BbsEngineFactory;
import io.sugo.pio.engine.bbs.BbsEngineParams;
import io.sugo.pio.engine.bbs.params.BbsDatasourceParams;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MmwJdbcHose;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineFactory;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class BbsTraining extends AbstractTraining{
    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        String timeColumn = "1";
        String url = "jdbc:postgresql://192.168.0.210:5432/mmw";
        String table = "mmq";
        String username = "postgres";
        String password = "123456";
        int count = 100000;
        int par = 1;
        String[] pNames ={"item_url", "parlabel","sublabel", "title", "content"};

        String table2 = "sbitem";
        String[] pNames2 ={"item_url", "catetory", "inf", "itemname", "descrip", "id"};
        MmwJdbcHose itemJdbcEventHost = new MmwJdbcHose(timeColumn, url, table2, username, password, count, par, pNames2);

        int sampleNum = 400;
        String[] labels = {"准备怀孕圈", "难孕难育圈", "孕8-10月圈", "生男生女圈", "宝宝取名圈", "宝宝营养辅食", "宝宝常见病圈", "早教幼教圈", "母婴用品使用讨论"};
        MmwJdbcHose mmwJdbcEventHose = new MmwJdbcHose(timeColumn, url, table, username, password, count, par, pNames);
        FileUtil.deleteDirectory(new File(BbsResource.PATH));
        Repository repository = new LocalFileRepository(BbsResource.PATH);
        BbsEngineParams bbsEngineParams = new BbsEngineParams(sampleNum, labels);
        EngineFactory engineFactory = new BbsEngineFactory(mmwJdbcEventHose,itemJdbcEventHost,repository, bbsEngineParams);
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
