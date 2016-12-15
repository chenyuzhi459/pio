package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaSparkContext;
import sugo.io.pio.engine.Preparator;
import sugo.io.pio.engine.template.data.TemplatePreparedData;
import sugo.io.pio.engine.template.data.TemplateTrainingData;

/**
 */
public class TemplatePreparator implements Preparator<TemplateTrainingData, TemplatePreparedData> {
    @Override
    public TemplatePreparedData prepare(JavaSparkContext sc, TemplateTrainingData td) {
        return new TemplatePreparedData(td.getData());
    }
}
