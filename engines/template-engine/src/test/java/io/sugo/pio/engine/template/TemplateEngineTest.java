package io.sugo.pio.engine.template;

import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.Algorithm;
import io.sugo.pio.engine.training.DataSource;
import io.sugo.pio.engine.training.Model;
import io.sugo.pio.engine.training.Preparator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.template.data.input.MovielenBatchEventHose;

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

        Model<TemplateModelData> model = factory.createModel();
        Repository repository = new LocalFileRepository("/tmp/modelfile");

        model.save(modelData, repository);
        System.out.print("ok");
    }

    @Test
    public void test1() {
        Object o = this.getClass().getClassLoader().getResource("movielen100k/sample_movielens_data.txt");
        System.out.println(o);
    }
}