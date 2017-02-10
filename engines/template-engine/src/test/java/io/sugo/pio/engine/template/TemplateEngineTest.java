package io.sugo.pio.engine.template;

import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;
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
        Repository repository = new LocalFileRepository("/tmp/modelfile");
        TemplateEngineFactory factory = new TemplateEngineFactory(movielenBatchEventHose, repository);
        Engine engine = factory.createEngine();
        engine.train(sc);
        System.out.print("ok");
    }

    @Test
    public void test1() {
        Object o = this.getClass().getClassLoader().getResource("movielen100k/sample_movielens_data.txt");
        System.out.println(o);
    }
}
