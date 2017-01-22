package io.sugo.pio.spark.modeling.prediction;

import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.spark.SparkOperator;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;

/**
 */
public abstract class AbstractLearner extends SparkOperator {
    protected final SparkOperation sparkOperation;
//    private final InputPort exampleSetInput = (InputPort)getInputPorts().createPort("training set");
    private final InputPort exampleSetInput;
    private final OutputPort modelOutput;
//    private final OutputPort modelOutput = createOutputPort("model");
//    private final OutputPort performanceOutput = createOutputPort("estimated performance", this.canEstimatePerformance());
//    private final OutputPort weightsOutput = createOutputPort("weights", this.canCalculateWeights());
//    private final OutputPort exampleSetOutput = createOutputPort("exampleSet");

    public AbstractLearner(SparkOperation sparkOperation,
                           InputPort exampleSetInput, OutputPort modelOutput) {
        this.exampleSetInput = exampleSetInput;
        this.modelOutput = modelOutput;
        this.sparkOperation = sparkOperation;
    }

    public void doWork() {
        HadoopExampleSet inputHes = getHesFromInputPort(exampleSetInput);
        PredictionModel model = learn(inputHes);
        modelOutput.deliver(model);
    }

    public abstract PredictionModel learn(HadoopExampleSet exampleSet);

    public abstract boolean canEstimatePerformance();

    public abstract boolean canCalculateWeights();

    public static void main(String[] args) {
        System.out.println();
    }
}
