package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.detail.data.DetailModelData;
import io.sugo.pio.engine.detail.data.DetailPreparedData;
import io.sugo.pio.engine.detail.data.DetailTrainingData;
import io.sugo.pio.engine.detail.engine.DetailAlgorithm;
import io.sugo.pio.engine.detail.engine.DetailDataSource;
import io.sugo.pio.engine.detail.engine.DetailModel;
import io.sugo.pio.engine.detail.engine.DetailPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class DetailEngine extends Engine<DetailTrainingData, DetailPreparedData, DetailModelData> {
    private final BatchEventHose batchEventHose;
    private final Repository repository;

    @JsonCreator
    public DetailEngine(@JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                               @JsonProperty("repository") Repository repository) {
        super(null);
        this.batchEventHose = batchEventHose;
        this.repository = repository;
    }
    @Override
    protected DataSource<DetailTrainingData> createDatasource(Params params) {
        return new DetailDataSource(batchEventHose);
    }

    @Override
    protected Preparator<DetailTrainingData, DetailPreparedData> createPreparator(Params params) {
        return new DetailPreparator();
    }

    @Override
    protected Algorithm<DetailPreparedData, DetailModelData> createAlgorithm(Params params) {
        return new DetailAlgorithm();
    }

    @Override
    protected Model<DetailModelData> createModel() {
        return new DetailModel(repository);
    }
}
