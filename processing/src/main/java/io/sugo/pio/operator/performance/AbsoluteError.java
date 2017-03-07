package io.sugo.pio.operator.performance;

import io.sugo.pio.i18n.I18N;

/**
 * The absolute error: <i>Sum(|label-predicted|)/#examples</i>. Mean absolue error is the average of
 * the difference between predicted and actual value in all test cases; it is the average prediction
 * error.
 * 
 */
public class AbsoluteError extends SimpleCriterion {

	private static final long serialVersionUID = 1113614384637128963L;

	public AbsoluteError() {}

	public AbsoluteError(AbsoluteError ae) {
		super(ae);
	}

	@Override
	public double countExample(double label, double predictedLabel) {
		double dif = Math.abs(label - predictedLabel);
		return dif;
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.AbsoluteError.absolute_error");
	}

	@Override
	public String getDescription() {
		return "Average absolute deviation of the prediction from the actual value";
	}
}
