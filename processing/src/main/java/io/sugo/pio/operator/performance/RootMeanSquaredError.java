package io.sugo.pio.operator.performance;

import io.sugo.pio.i18n.I18N;

/**
 * The root-mean-squared error. Mean-squared error is the most commonly used measure of success of
 * numeric prediction, and root mean-squared error is the square root of mean-squared-error, take to
 * give it the same dimensions as the predicted values themselves. This method exaggerates the
 * prediction error - the difference between prediction value and actual value of a test case - of
 * test cases in which the prediction error is larger than the others. If this number is
 * significantly greater than the mean absolute error, it means that there are test cases in which
 * the prediction error is significantly greater than the average prediction error.
 * 
 */
public class RootMeanSquaredError extends SimpleCriterion {

	private static final long serialVersionUID = -4425511584684855855L;

	public RootMeanSquaredError() {}

	public RootMeanSquaredError(RootMeanSquaredError sc) {
		super(sc);
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.RootMeanSquaredError.root_mean_squared_error");
	}

	/** Calculates the error for the current example. */
	@Override
	public double countExample(double label, double predictedLabel) {
		double dif = label - predictedLabel;
		return dif * dif;
	}

	/** Applies a square root to the given value. */
	@Override
	public double transform(double value) {
		return Math.sqrt(value);
	}

	@Override
	public String getDescription() {
		return "Averaged root-mean-squared error";
	}
}