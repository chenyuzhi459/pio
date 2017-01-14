package io.sugo.pio.operator.learner;

import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.performance.PerformanceVector;

/**
 */
public interface Learner {
    /**
     * Trains a model. This method should be called by apply() and is implemented by subclasses.
     */
    public Model learn(ExampleSet exampleSet) throws OperatorException;

    /** Returns the name of the learner. */
    public String getName();

    /**
     * Most learners will return false since they are not able to estimate the learning performance.
     * However, if a learning scheme is able to calculate the performance (e.g. Xi-Alpha estimation
     * of a SVM) it should return true.
     */
    public boolean shouldEstimatePerformance();

    /**
     * Most learners should throw an exception if they are not able to estimate the learning
     * performance. However, if a learning scheme is able to calculate the performance (e.g.
     * Xi-Alpha estimation of a SVM) it should return a performance vector containing the estimated
     * performance.
     */
    public PerformanceVector getEstimatedPerformance() throws OperatorException;

    /**
     * Most learners will return false since they are not able to calculate attribute weights.
     * However, if a learning scheme is able to calculate weights (e.g. the normal vector of a SVM)
     * it should return true.
     */
    public boolean shouldCalculateWeights();

    /**
     * Most learners should throw an exception if they are not able to calculate attribute weights.
     * However, if a learning scheme is able to calculate weights (e.g. the normal vector of a SVM)
     * it should return an AttributeWeights object.
     */
    public AttributeWeights getWeights(ExampleSet eSet) throws OperatorException;
}
