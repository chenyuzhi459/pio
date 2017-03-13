package io.sugo.pio.operator.performance;

import io.sugo.pio.i18n.I18N;

/**
 * The squared error. Sums up the square of the absolute deviations and divides the sum by the
 * number of examples.
 * 
 */
public class SquaredError extends SimpleCriterion {

	private static final long serialVersionUID = 322984719296835789L;

	public SquaredError() {}

	public SquaredError(SquaredError se) {
		super(se);
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.SquaredError.squared_error");
	}

	/** Calculates the error for the current example. */
	@Override
	public double countExample(double label, double predictedLabel) {
		double dif = label - predictedLabel;
		return dif * dif;
	}

	@Override
	public String getDescription() {
		return "Averaged squared error";
	}
}
