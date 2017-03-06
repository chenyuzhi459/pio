package io.sugo.pio.operator.learner.functions.kernel;

import io.sugo.pio.operator.learner.AbstractLearner;
import io.sugo.pio.operator.learner.PredictionModel;


/**
 * This class is the common super class of all KernelModel producing learners. It returns the
 * appropriate model class for metaData Processing.
 */
public abstract class AbstractKernelBasedLearner extends AbstractLearner {

    public AbstractKernelBasedLearner() {
        super();
    }

    @Override
    public Class<? extends PredictionModel> getModelClass() {
        return KernelModel.class;
    }
}
