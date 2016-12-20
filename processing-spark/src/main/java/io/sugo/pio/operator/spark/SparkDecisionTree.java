package io.sugo.pio.operator.spark;

import io.sugo.pio.datahandler.HadoopExampleSet;
import io.sugo.pio.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;

/**
 */
public class SparkDecisionTree extends AbstractSparkLearner {
    public SparkDecisionTree(OperatorDescription description) {
        super(description, SparkOperation.DecisionTree);
    }

    @Override
    public boolean canEstimatePerformance() {
        return false;
    }

    @Override
    public boolean canCalculateWeights() {
        return false;
    }

    @Override
    protected void postProcessModel(PredictionModel model, HadoopExampleSet exampleSet) {
    }
}
