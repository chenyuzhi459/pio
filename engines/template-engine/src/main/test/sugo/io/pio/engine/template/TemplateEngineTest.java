package sugo.io.pio.engine.template;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import sugo.io.pio.engine.Algorithm;
import sugo.io.pio.engine.DataSource;
import sugo.io.pio.engine.Preparator;
import sugo.io.pio.engine.template.data.TemplateModelData;
import sugo.io.pio.engine.template.data.TemplatePreparedData;
import sugo.io.pio.engine.template.data.TemplateTrainingData;
import sugo.io.pio.engine.template.data.input.MovielenBatchEventHose;

/**
 */
public class TemplateEngineTest {
    @Test
    public void test() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local");
        sparkConf.setAppName("template-engine");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        MovielenBatchEventHose movielenBatchEventHose = new MovielenBatchEventHose("resources/movielen100k/sample_movielens_data.txt", "::");

        TemplateEngineFactory factory = new TemplateEngineFactory();
        DataSource<TemplateTrainingData> datasource =  factory.createDatasource();
        TemplateTrainingData trainingData = datasource.readTraining(sc, movielenBatchEventHose);

        Preparator<TemplateTrainingData, TemplatePreparedData> preparator = factory.createPreparator();
        TemplatePreparedData preparedData = preparator.prepare(sc, trainingData);

        Algorithm<TemplatePreparedData, TemplateModelData> modelDataAlgorithm = factory.createAlgorithm();
        TemplateModelData modelData = modelDataAlgorithm.train(sc, preparedData);

        System.out.print("ok");
    }
}