package io.sugo.pio.engine.userFeatureExtractionNew.main;

import com.typesafe.config.Config;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.userFeatureExtractionNew.engine.UserFeatureExtractionEngine;
import io.sugo.pio.engine.userFeatureExtractionNew.engine.UserFeatureExtractionEngineParams;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by penghuan on 2017/4/27.
 */
public class UserFeatureExtractionLocal {

    public void run(Config config) {
        EngineParams engineParams = new UserFeatureExtractionEngineParams();
        Engine engine = new UserFeatureExtractionEngine(config, engineParams);
        SparkConf conf = new SparkConf();
        conf.setMaster("local[4]").setAppName("user-feature-extraction");
        JavaSparkContext jsc = new JavaSparkContext(SparkContext.getOrCreate(conf));
        engine.train(jsc);
    }
}
