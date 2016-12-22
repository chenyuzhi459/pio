package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;

import java.util.LinkedList;
import java.util.List;

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

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
