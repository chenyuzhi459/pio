package io.sugo.pio.operator.performance;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.tools.Tools;


/**
 * The average relative error: <i>Sum(|label-predicted|/label)/#examples</i>. The relative error of
 * label 0 and prediction 0 is defined as 0, the relative error of label 0 and prediction != 0 is
 * infinite.
 * 
 */
public class RelativeError extends SimpleCriterion {

	private static final long serialVersionUID = 203943264201733699L;

	public RelativeError() {}

	public RelativeError(RelativeError sc) {
		super(sc);
	}

	@Override
	public double countExample(double label, double predictedLabel) {
		double diff = Math.abs(label - predictedLabel);
		double absLabel = Math.abs(label);
		if (Tools.isZero(absLabel)) {
			return Double.NaN;
		} else {
			return diff / absLabel;
		}
	}

	/**
	 * Indicates wether or not percentage format should be used in the {@link #toString} method. The
	 * default implementation returns false.
	 */
	@Override
	public boolean formatPercent() {
		return true;
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.RelativeError.relative_error");
	}

	@Override
	public String getDescription() {
		return "Average relative error (average of absolute deviation of the prediction from the actual value divided by actual value)";
	}
}
