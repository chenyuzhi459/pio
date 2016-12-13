package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaSparkContext;
import sugo.io.pio.engine.Preparator;
import sugo.io.pio.engine.template.data.TemplatePreparedData;

/**
 */
public class TemplatePreparator implements Preparator<TemplatePreparedData> {
    @Override
    public TemplatePreparedData prepare(JavaSparkContext sc) {
        return null;
    }
}
