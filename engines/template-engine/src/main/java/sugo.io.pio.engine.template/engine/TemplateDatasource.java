package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaSparkContext;
import sugo.io.pio.engine.DataSource;
import sugo.io.pio.engine.template.data.TemplateTrainingData;

/**
 */
public class TemplateDatasource implements DataSource<TemplateTrainingData> {
    @Override
    public TemplateTrainingData readTraining(JavaSparkContext sc) {
        return null;
    }
}
