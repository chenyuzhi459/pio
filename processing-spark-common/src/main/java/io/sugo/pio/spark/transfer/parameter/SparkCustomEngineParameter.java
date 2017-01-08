package io.sugo.pio.spark.transfer.parameter;

import io.sugo.pio.spark.engine.EngineFactory;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.output.Repository;

/**
 */
public class SparkCustomEngineParameter<TD, PD, MD, SD> extends SparkParameter {
    private EngineFactory<TD, PD, MD, SD> engineFactory;
    private Repository repository;
    private BatchEventHose batchEventHose;

    public EngineFactory getEngineFactory() {
        return engineFactory;
    }

    public void setEngineFactory(EngineFactory engineFactory) {
        this.engineFactory = engineFactory;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    public void setBatchEventHose(BatchEventHose batchEventHose) {
        this.batchEventHose = batchEventHose;
    }
}
