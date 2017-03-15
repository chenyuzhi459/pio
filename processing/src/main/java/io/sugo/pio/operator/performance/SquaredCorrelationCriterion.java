package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.i18n.I18N;

/**
 * Computes the square of the empirical corellation coefficient 'r' between label and prediction.
 * Eith P=prediction, L=label, V=Variance, Cov=Covariance we calculate r by: <br>
 * Cov(L,P) / sqrt(V(L)*V(P)). Uses the calculation of the superclass.
 */
public class SquaredCorrelationCriterion extends CorrelationCriterion {

    private static final long serialVersionUID = 8751373179064203312L;

    @Override
    public String getDescription() {
        return "Returns the squared correlation coefficient between the label and predicted label.";
    }

    @Override
    public double getMikroAverage() {
        double r = super.getMikroAverage();
        return r * r;
    }

    @Override
    @JsonProperty
    public String getName() {
        return I18N.getMessage("pio.SquaredCorrelationCriterion.squared_correlation");
    }
}
