package io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm;

import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExample;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;


/**
 * The interface of all SVMs.
 *
 */
public interface SVMInterface {

	/** Initializes this SVM. */
	public void init(Kernel kernel, SVMExamples examples);

	/** Train this SVM. */
	public void train() throws OperatorException;

	/** Perform a prediction of label for all examples. */
	public void predict(SVMExamples examples);

	/** Perform a prediction of label for all examples. */
	public double predict(SVMExample sVMExample);

	/** Returns the weights of all features. */
	public double[] getWeights();

	/** Returns the value of b. */
	public double getB();
}
