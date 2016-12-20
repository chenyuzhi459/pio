package io.sugo.pio.operator.spark;

import io.sugo.pio.datahandler.HadoopExampleSet;
import io.sugo.pio.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.modeling.prediction.AbstractLearner;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;

/**
 */
public abstract class AbstractSparkLearner<M extends PredictionModel> extends AbstractLearner {
    public AbstractSparkLearner(OperatorDescription description, SparkOperation sparkOperation) {
        super(description, sparkOperation);
    }

    @Override
    public PredictionModel learn(HadoopExampleSet exampleSet) {
        return null;
    }

    protected abstract void postProcessModel(M model, HadoopExampleSet exampleSet);
}
