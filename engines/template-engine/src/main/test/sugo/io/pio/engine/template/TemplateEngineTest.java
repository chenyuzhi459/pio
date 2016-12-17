package sugo.io.pio.engine.template;

import org.apache.spark.MapOutputStatistics;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import sugo.io.pio.data.output.Repository;
import sugo.io.pio.engine.Algorithm;
import sugo.io.pio.engine.DataSource;
import sugo.io.pio.engine.Model;
import sugo.io.pio.engine.Preparator;
import sugo.io.pio.engine.template.data.TemplateModelData;
import sugo.io.pio.engine.template.data.TemplatePreparedData;
import sugo.io.pio.engine.template.data.TemplateTrainingData;
import sugo.io.pio.engine.template.data.input.MovielenBatchEventHose;
import sugo.io.pio.engine.template.data.output.LocalFileRepository;

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

        Model<TemplateModelData> model = factory.createModel();
        Repository repository = new LocalFileRepository("/tmp/modelfile");

        model.save(modelData, repository);
        TemplateModelData model2 = model.read(repository);

        System.out.print("ok");
    }
}