package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.tools.Tools;


/**
 * The average relative error in a lenient way of calculation: <i>Sum(|label-predicted|/max(|label|,
 * |predicted|))/#examples</i>. The relative error of label 0 and prediction 0 is defined as 0.
 */
public class LenientRelativeError extends SimpleCriterion {

    private static final long serialVersionUID = -6816726234908353254L;

    public LenientRelativeError() {
    }

    public LenientRelativeError(LenientRelativeError sc) {
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
            return diff / Math.max(absLabel, absPrediction);
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
    @JsonProperty
    public String getName() {
        return I18N.getMessage("pio.LenientRelativeError.relative_error_lenient");
    }

    @Override
    public String getDescription() {
        return "Average lenient relative error (average of absolute deviation of the prediction from the actual value divided by maximum of the actual value and the prediction)";
    }
}
