package io.sugo.pio.engine.template;

import io.sugo.pio.spark.engine.Algorithm;
import io.sugo.pio.spark.engine.DataSource;
import io.sugo.pio.spark.engine.Model;
import io.sugo.pio.spark.engine.Preparator;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.template.data.input.MovielenBatchEventHose;
import io.sugo.pio.spark.engine.data.output.LocalFileRepository;

/**
 */
public class TemplateEngineTest {
    @Test
    public void test() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local");
        sparkConf.setAppName("template-engine");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        MovielenBatchEventHose movielenBatchEventHose = new MovielenBatchEventHose(this.getClass().getClassLoader().getResource("movielen100k/sample_movielens_data.txt").getPath(), "::");

        TemplateEngineFactory factory = new TemplateEngineFactory(movielenBatchEventHose);
        DataSource<TemplateTrainingData> datasource =  factory.createDatasource();
        TemplateTrainingData trainingData = datasource.readTraining(sc);

        Preparator<TemplateTrainingData, TemplatePreparedData> preparator = factory.createPreparator();
        TemplatePreparedData preparedData = preparator.prepare(sc, trainingData);

        Algorithm<TemplatePreparedData, TemplateModelData> modelDataAlgorithm = factory.createAlgorithm();
        TemplateModelData modelData = modelDataAlgorithm.train(sc, preparedData);

        Model<TemplateModelData, TemplateModelData> model = factory.createModel();
        Repository repository = new LocalFileRepository("/tmp/modelfile");

        model.save(modelData, repository);
        TemplateModelData model2 = model.load(repository);

        System.out.print("ok");
    }

    @Test
    public void test1() {
        Object o = this.getClass().getClassLoader().getResource("movielen100k/sample_movielens_data.txt");
        System.out.println(o);
    }
}