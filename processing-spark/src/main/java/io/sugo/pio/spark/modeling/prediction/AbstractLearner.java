package io.sugo.pio.spark.modeling.prediction;

import io.sugo.pio.spark.SparkOperator;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 */
public abstract class AbstractLearner extends SparkOperator {
    private final SparkOperation sparkOperation;
    private final InputPort exampleSetInput = (InputPort)getInputPorts().createPort("training set");
    private final OutputPort modelOutput = createOutputPort("model");
    private final OutputPort performanceOutput = createOutputPort("estimated performance", this.canEstimatePerformance());
    private final OutputPort weightsOutput = createOutputPort("weights", this.canCalculateWeights());
    private final OutputPort exampleSetOutput = createOutputPort("exampleSet");

    public AbstractLearner(OperatorDescription description, SparkOperation sparkOperation) {
        super(description);
        this.sparkOperation = sparkOperation;
    }

    public abstract PredictionModel learn(HadoopExampleSet exampleSet);

    public abstract boolean canEstimatePerformance();

    public abstract boolean canCalculateWeights();
}
