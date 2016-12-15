package sugo.io.pio.engine.template.data;

import org.apache.spark.ml.recommendation.ALSModel;

/**
 */
public class TemplateModelData {
    private final ALSModel model;

    public TemplateModelData(ALSModel model) {
        this.model = model;
    }

    public ALSModel getModel() {
        return model;
    }
}
