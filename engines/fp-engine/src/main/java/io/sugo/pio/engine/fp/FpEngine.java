package io.sugo.pio.engine.fp;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.fp.data.FpModelData;
import io.sugo.pio.engine.fp.data.FpPreparaData;
import io.sugo.pio.engine.fp.data.FpTrainingData;
import io.sugo.pio.engine.fp.engine.FpAlgorithm;
import io.sugo.pio.engine.fp.engine.FpDatasource;
import io.sugo.pio.engine.fp.engine.FpModel;
import io.sugo.pio.engine.fp.engine.FpPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class FpEngine extends Engine<FpTrainingData, FpPreparaData, FpModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final FpEngineParams fpEngineParams;

    public FpEngine(PropertyHose propertyHose,
                    BatchEventHose batchEventHose,
                    Repository repository,
                    FpEngineParams fpEngineParams) {
        super(fpEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.fpEngineParams = fpEngineParams;

    }

    @Override
    public DataSource<FpTrainingData> createDatasource(Params params) {
        return new FpDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<FpTrainingData, FpPreparaData> createPreparator(Params params) {
        return new FpPreparator();
    }

    @Override
    public Algorithm<FpPreparaData, FpModelData> createAlgorithm(Params params) {
        return new FpAlgorithm();
    }

    @Override
    public Model<FpModelData> createModel() {
        return new FpModel(repository);
    }
}
