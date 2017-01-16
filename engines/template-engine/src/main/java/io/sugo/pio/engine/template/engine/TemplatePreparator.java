package io.sugo.pio.engine.template.engine;

import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.training.Preparator;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class TemplatePreparator implements Preparator<TemplateTrainingData, TemplatePreparedData> {
    @Override
    public TemplatePreparedData prepare(JavaSparkContext sc, TemplateTrainingData td) {
        return new TemplatePreparedData(td.getData());
    }
}
