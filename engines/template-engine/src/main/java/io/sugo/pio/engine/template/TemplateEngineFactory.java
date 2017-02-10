package io.sugo.pio.engine.template;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.engine.*;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class TemplateEngineFactory implements EngineFactory<TemplateTrainingData, TemplatePreparedData, TemplateModelData> {
    private final BatchEventHose eventHose;
    private final Repository repository;

    public TemplateEngineFactory(BatchEventHose eventHose, Repository repository) {
        this.eventHose = eventHose;
        this.repository = repository;
    }


    @Override
    public Engine createEngine() {
        return new TemplateEngine(eventHose, repository);
    }
}
