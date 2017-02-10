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
public class TextSimilarEngine extends Engine<TextSimilarTrainingData, TextSimilarPreparaData, TextSimilarModelData>{
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final TextSimilarEngineParams textSimilarEngineParams;

    public TextSimilarEngine(PropertyHose propertyHose,
                             BatchEventHose batchEventHose,
                             Repository repository,
                             TextSimilarEngineParams textSimilarEngineParams) {
        super(textSimilarEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.textSimilarEngineParams = textSimilarEngineParams;
    }

    @Override
    protected DataSource<TextSimilarTrainingData> createDatasource(Params params) {
        return new TextSimilarDatasource(propertyHose, batchEventHose);
    }

    @Override
    protected Preparator<TextSimilarTrainingData, TextSimilarPreparaData> createPreparator(Params params) {
        return new TextSimilarPreparator();
    }

    @Override
    protected Algorithm<TextSimilarPreparaData, TextSimilarModelData> createAlgorithm(Params params) {
        return new TextSimilarAlgorithm();
    }

    @Override
    protected Model<TextSimilarModelData> createModel() {
        return new TextSimilarModel(repository);
    }
}
