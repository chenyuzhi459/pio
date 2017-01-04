package io.sugo.pio.spark.runner;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.spark.SparkModule;
import io.sugo.pio.spark.engine.*;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.output.Repository;
import io.sugo.pio.spark.transfer.parameter.SparkCustomEngineParameter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ServiceLoader;

/**
 */
public class SparkCustomEngineRunner {
    public static void main(String[] encodedArgs) throws Exception {
        ServiceLoader<SparkModule> serviceLoader = ServiceLoader
                .load(SparkModule.class);
        ObjectMapper mapper = new ObjectMapper();
        for(SparkModule iModule : serviceLoader) {
            for(Module module: iModule.getJacksonModules()) {
                mapper.registerModule(module);
            }
        }

        SparkConf sparkConf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        SparkCustomEngineParameter parameter = mapper.readValue(encodedArgs[0], SparkCustomEngineParameter.class);

        EngineFactory factory = parameter.getEngineFactory();
        DataSource datasource =  factory.createDatasource();
        BatchEventHose batchEventHose = parameter.getBatchEventHose();
        Object trainingData = datasource.readTraining(sc, batchEventHose);
        Preparator preparator = factory.createPreparator();
        Object preparedData = preparator.prepare(sc, trainingData);

        Algorithm modelDataAlgorithm = factory.createAlgorithm();
        Object modelData = modelDataAlgorithm.train(sc, preparedData);

        Model model = factory.createModel();
        Repository repository = parameter.getRepository();

        model.save(modelData, repository);
    }
}
