package io.sugo.pio.engine.userFeatureExtractionNew.main;

import com.typesafe.config.Config;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.userFeatureExtractionNew.engine.UserFeatureExtractionEngine;
import io.sugo.pio.engine.userFeatureExtractionNew.engine.UserFeatureExtractionEngineParams;
import io.sugo.pio.engine.userFeatureExtractionNew.tools.UserFeatureExtractionProperty;
import org.apache.spark.api.java.JavaSparkContext;
import spark.jobserver.api.JobEnvironment;
import spark.jobserver.japi.JSparkJob;

/**
 * Created by penghuan on 2017/4/25.
 */
public class UserFeatureExtractionSparkJobServer implements JSparkJob<String> {
    @Override
    public String run(JavaSparkContext jsc, JobEnvironment runtime, Config config) {
        EngineParams engineParams = new UserFeatureExtractionEngineParams();
        Engine engine = new UserFeatureExtractionEngine(config, engineParams);
        engine.train(jsc);
        return "";
    }

    @Override
    public Config verify(JavaSparkContext jsc, JobEnvironment runtime, Config config) {
        for (String key: UserFeatureExtractionProperty.keys.values()) {
            String value = config.getString(key);
            if (value == null || value.isEmpty()) {
                throw new RuntimeException(String.format("%s not found", key));
            }
        }
        return config;
    }
}
