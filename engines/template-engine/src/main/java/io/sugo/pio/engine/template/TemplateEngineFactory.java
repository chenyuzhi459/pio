package io.sugo.pio.engine.template;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.engine.TemplateAlgorithm;
import io.sugo.pio.engine.template.engine.TemplateModel;
import io.sugo.pio.engine.template.engine.TemplatePreparator;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.template.engine.TemplateDatasource;
import io.sugo.pio.engine.training.*;

/**
 */
public class TemplateEngineFactory implements EngineFactory<TemplateTrainingData, TemplatePreparedData, TemplateModelData> {
    private BatchEventHose eventHose;

    public TemplateEngineFactory(BatchEventHose eventHose) {
        this.eventHose = eventHose;
    }

    @Override
    public DataSource<TemplateTrainingData> createDatasource() {
        return new TemplateDatasource(eventHose);
    }

    @Override
    public Preparator<TemplateTrainingData, TemplatePreparedData> createPreparator() {
        return new TemplatePreparator();
    }

    @Override
    public Algorithm<TemplatePreparedData, TemplateModelData> createAlgorithm() {
        return new TemplateAlgorithm();
    }

    @Override
    public Model<TemplateModelData> createModel() {
        return new TemplateModel();
    }
}
