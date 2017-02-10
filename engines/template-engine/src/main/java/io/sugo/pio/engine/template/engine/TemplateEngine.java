package io.sugo.pio.engine.template.engine;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.data.TemplatePreparedData;
import io.sugo.pio.engine.template.data.TemplateTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class TemplateEngine extends Engine<TemplateTrainingData, TemplatePreparedData, TemplateModelData> {
    private final BatchEventHose eventHose;
    private final Repository repository;

    public TemplateEngine(BatchEventHose eventHose, Repository repository) {
        super(null);
        this.eventHose = eventHose;
        this.repository = repository;
    }

    @Override
    public DataSource<TemplateTrainingData> createDatasource(Params params) {
        return new TemplateDatasource(eventHose);
    }

    @Override
    public Preparator<TemplateTrainingData, TemplatePreparedData> createPreparator(Params params) {
        return new TemplatePreparator();
    }

    @Override
    public Algorithm<TemplatePreparedData, TemplateModelData> createAlgorithm(Params params) {
        return new TemplateAlgorithm();
    }

    @Override
    public Model<TemplateModelData> createModel() {
        return new TemplateModel(repository);
    }
}
