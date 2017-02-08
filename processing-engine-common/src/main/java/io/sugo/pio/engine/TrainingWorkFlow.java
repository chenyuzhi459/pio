package io.sugo.pio.engine;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import io.sugo.pio.engine.training.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.ServiceLoader;

/**
 */
public class TrainingWorkFlow {
    public static void main(String[] args) throws IOException {
        SparkConf sparkConf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        TrainingObjectMapper mapper = new TrainingObjectMapper();
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
        EngineFactory engineFactory = trainingConfig.getEngineFactory();
        DataSource dataSource = engineFactory.createDatasource();
        Preparator preparator = engineFactory.createPreparator();
        Model model = engineFactory.createModel();
        Algorithm alg = engineFactory.createAlgorithm();

        Object trainingData = dataSource.readTraining(sc);
        Object preparedData = preparator.prepare(sc, trainingData);
        Object modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }

    private static class TrainingObjectMapper extends ObjectMapper {
        public TrainingObjectMapper()
        {
            this((JsonFactory)null);
        }

        public TrainingObjectMapper(TrainingObjectMapper mapper)
        {
            super(mapper);
        }

        public TrainingObjectMapper(JsonFactory factory)
        {
            super(factory);
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            configure(MapperFeature.AUTO_DETECT_GETTERS, false);
            configure(MapperFeature.AUTO_DETECT_FIELDS, false);
            configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
            configure(MapperFeature.AUTO_DETECT_SETTERS, false);
            configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, false);
            configure(SerializationFeature.INDENT_OUTPUT, false);
        }

        @Override
        public ObjectMapper copy()
        {
            return new TrainingObjectMapper(this);
        }
    }
}
