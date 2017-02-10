package io.sugo.pio.engine;

import com.fasterxml.jackson.databind.Module;
import io.sugo.pio.engine.training.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;

/**s
 */
public class EvaluationWorkFlow {
    public static void main(String[] args) throws IOException {
        EngineObjectMapper mapper = new EngineObjectMapper();
        ServiceLoader<EngineModule> engineModules = ServiceLoader
                .load(EngineModule.class);

        for(EngineModule iModule : engineModules) {
            for(Module module: iModule.getJacksonModules()) {
                mapper.registerModule(module);
            }
        }

        ServiceLoader<EngineExtensionModule> engineExtensionModules = ServiceLoader
                .load(EngineExtensionModule.class);
        for(EngineExtensionModule iModule : engineExtensionModules) {
            for(Module module: iModule.getJacksonModules()) {
                mapper.registerModule(module);
            }
        }

        if (args.length < 1) {
            System.exit(1);
        }

        String trainingConfigStr = args[0];
        TrainingConfig trainingConfig = mapper.readValue(trainingConfigStr, TrainingConfig.class);
        SparkConf sparkConf = new SparkConf();
        Map<String, String> context = trainingConfig.getSparkConfSettings();
        for(Map.Entry<String, String> entry: context.entrySet()) {
            sparkConf.set(entry.getKey(), entry.getValue());
        }

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        SparkContextSettings sparkContextSettings = trainingConfig.getSparkContextSettings();
        if (null != sparkContextSettings.getCheckPointDir()) {
            sc.setCheckpointDir(sparkContextSettings.getCheckPointDir());
        }

        EngineFactory engineFactory = trainingConfig.getEngineFactory();
        Engine engine = engineFactory.createEngine();
        engine.train(sc);
    }
}
