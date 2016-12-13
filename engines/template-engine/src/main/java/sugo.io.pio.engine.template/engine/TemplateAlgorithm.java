package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaSparkContext;
import sugo.io.pio.engine.Algorithm;
import sugo.io.pio.engine.template.data.TemplateModelData;

/**
 */
public class TemplateAlgorithm implements Algorithm<TemplateModelData> {
    @Override
    public TemplateModelData train(JavaSparkContext sc) {
        return null;
    }
}
