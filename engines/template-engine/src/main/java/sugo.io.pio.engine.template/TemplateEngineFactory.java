package sugo.io.pio.engine.template;

import sugo.io.pio.engine.*;
import sugo.io.pio.engine.template.data.TemplateModelData;
import sugo.io.pio.engine.template.data.TemplatePreparedData;
import sugo.io.pio.engine.template.data.TemplateTrainingData;
import sugo.io.pio.engine.template.engine.TemplateAlgorithm;
import sugo.io.pio.engine.template.engine.TemplateDatasource;
import sugo.io.pio.engine.template.engine.TemplatePreparator;

/**
 */
public class TemplateEngineFactory implements EngineFactory<TemplateTrainingData, TemplatePreparedData, TemplateModelData> {
    @Override
    public DataSource<TemplateTrainingData> createDatasource() {
        return new TemplateDatasource();
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
        return null;
    }
}
