package io.sugo.pio.operator.performance;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.tools.Tools;


/**
 * The average relative error in a strict way of calculation: <i>Sum(|label-predicted|/min(|label|,
 * |predicted|))/#examples</i>. The relative error of label 0 and prediction 0 is defined as 0. If
 * the minimum of label and prediction is 0, the relative error is defined as infinite.
 * 
 */
public class StrictRelativeError extends SimpleCriterion {

	private static final long serialVersionUID = 8055914052886853327L;

	public StrictRelativeError() {}

	public StrictRelativeError(StrictRelativeError sc) {
		super(sc);
	}

	@Override
	public double countExample(double label, double predictedLabel) {
		double diff = Math.abs(label - predictedLabel);
		double absLabel = Math.abs(label);
		double absPrediction = Math.abs(predictedLabel);
		if (Tools.isZero(diff)) {
			return 0.0d;
		} else {
			double min = Math.min(absLabel, absPrediction);
			if (Tools.isZero(min)) {
				return Double.POSITIVE_INFINITY;
			} else {
				return diff / min;
			}
		}
	}

	/**
	 * Indicates whether or not percentage format should be used in the {@link #toString} method.
	 * The default implementation returns false.
	 */
	@Override
	public boolean formatPercent() {
		return true;
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.StrictRelativeError.relative_error_strict");
	}

	@Override
	public String getDescription() {
		return "Average strict relative error (average of absolute deviation of the prediction from the actual value divided by minimum of the actual value and the prediction)";
	}
}
