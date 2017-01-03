package io.sugo.pio.engine.template.engine;

import io.sugo.pio.spark.engine.Preparator;
import org.apache.spark.api.java.JavaSparkContext;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;

/**
 */
public class TemplatePreparator implements Preparator<TemplateTrainingData, TemplatePreparedData> {
    @Override
    public TemplatePreparedData prepare(JavaSparkContext sc, TemplateTrainingData td) {
        return new TemplatePreparedData(td.getData());
    }
}
