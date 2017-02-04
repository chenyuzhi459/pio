package io.sugo.pio.engine.textSimilar;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.textSimilar.data.TextSimilarModelData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarPreparaData;
import io.sugo.pio.engine.textSimilar.data.TextSimilarTrainingData;
import io.sugo.pio.engine.textSimilar.engine.TextSimilarAlgorithm;
import io.sugo.pio.engine.textSimilar.engine.TextSimilarDatasource;
import io.sugo.pio.engine.textSimilar.engine.TextSimilarModel;
import io.sugo.pio.engine.textSimilar.engine.TextSimilarPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class TextSimilarEngineFactory implements EngineFactory<TextSimilarTrainingData, TextSimilarPreparaData, TextSimilarModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;

    public TextSimilarEngineFactory(PropertyHose propertyHose,
                                    BatchEventHose batchEventHose,
                                    Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
    }

    @Override
    public DataSource<TextSimilarTrainingData> createDatasource() {
        return new TextSimilarDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<TextSimilarTrainingData, TextSimilarPreparaData> createPreparator() {
        return new TextSimilarPreparator();
    }

    @Override
    public Algorithm<TextSimilarPreparaData, TextSimilarModelData> createAlgorithm() {
        return new TextSimilarAlgorithm();
    }

    @Override
    public Model<TextSimilarModelData> createModel() {
        return new TextSimilarModel(repository);
    }
}
