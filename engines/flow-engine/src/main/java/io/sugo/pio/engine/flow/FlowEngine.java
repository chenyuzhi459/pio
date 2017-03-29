package io.sugo.pio.engine.flow;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.flow.data.FlowModelData;
import io.sugo.pio.engine.flow.data.FlowPreparaData;
import io.sugo.pio.engine.flow.data.FlowTrainingData;
import io.sugo.pio.engine.flow.engine.FlowAlgorithm;
import io.sugo.pio.engine.flow.engine.FlowDatasource;
import io.sugo.pio.engine.flow.engine.FlowModel;
import io.sugo.pio.engine.flow.engine.FlowPreparator;
import io.sugo.pio.engine.flow.eval.FlowEvalActualResult;
import io.sugo.pio.engine.flow.eval.FlowEvalIndicators;
import io.sugo.pio.engine.flow.eval.FlowEvalQuery;
import io.sugo.pio.engine.training.*;

/**
 */
public class FlowEngine extends Engine<FlowTrainingData, FlowPreparaData, FlowModelData, FlowEvalQuery, FlowEvalActualResult, FlowEvalIndicators> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final FlowEngineParams popEngineParams;

    public FlowEngine(PropertyHose propertyHose,
                      BatchEventHose batchEventHose,
                      Repository repository,
                      FlowEngineParams popEngineParams) {
        super(popEngineParams);
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.popEngineParams = popEngineParams;
    }

    @Override
    public DataSource<FlowTrainingData, FlowEvalQuery, FlowEvalActualResult> createDatasource(Params params) {
        return new FlowDatasource(propertyHose, batchEventHose);
    }

    @Override
    public Preparator<FlowTrainingData, FlowPreparaData> createPreparator(Params params) {
        return new FlowPreparator();
    }

    @Override
    public Algorithm<FlowPreparaData, FlowModelData> createAlgorithm(Params params) {
        return new FlowAlgorithm();
    }

    @Override
    public Model<FlowModelData> createModel() {
        return new FlowModel(repository);
    }

    @Override
    protected Evalution createEval() {
        return null;
    }

}
