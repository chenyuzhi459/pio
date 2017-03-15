package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;


/**
 * Computes the empirical corelation coefficient 'r' between label and prediction. For
 * <code>P=prediction, L=label, V=Variance, Cov=Covariance</code> we calculate r by: <br>
 * <code>Cov(L,P) / sqrt(V(L)*V(P))</code>.
 * <p>
 * Implementation hint: this implementation intensionally recomputes the mean and variance of
 * prediction and label despite the fact that they are available by the Attribute objects. The
 * reason: it can happen, that there are some examples which have a NaN as prediction or label, but
 * not both. In this case, mean and variance stored in tie Attributes and computed here can differ.
 */
public class CorrelationCriterion extends MeasuredPerformance {

    private static final long serialVersionUID = -8789903466296509903L;

    private Attribute labelAttribute;

    private Attribute predictedLabelAttribute;

    private Attribute weightAttribute;

    @JsonProperty
    private double exampleCount = 0;

    @JsonProperty
    private double sumLabel;

    @JsonProperty
    private double sumPredict;

    @JsonProperty
    private double sumLabelPredict;

    @JsonProperty
    private double sumLabelSqr;

    @JsonProperty
    private double sumPredictSqr;

    public CorrelationCriterion() {
    }

    public CorrelationCriterion(CorrelationCriterion sc) {
        super(sc);
        this.sumLabelPredict = sc.sumLabelPredict;
        this.sumLabelSqr = sc.sumLabelSqr;
        this.sumPredictSqr = sc.sumPredictSqr;
        this.sumLabel = sc.sumLabel;
        this.sumPredict = sc.sumPredict;
        this.exampleCount = sc.exampleCount;
        this.labelAttribute = (Attribute) sc.labelAttribute.clone();
        this.predictedLabelAttribute = (Attribute) sc.predictedLabelAttribute.clone();
        if (sc.weightAttribute != null) {
            this.weightAttribute = (Attribute) sc.weightAttribute.clone();
        }
    }

    @Override
    public double getExampleCount() {
        return exampleCount;
    }

    /**
     * Returns the maximum fitness of 1.0.
     */
    @Override
    public double getMaxFitness() {
        return 1.0d;
    }

    /**
     * Updates all sums needed to compute the correlation coefficient.
     */
    @Override
    public void countExample(Example example) {
        double label = example.getValue(labelAttribute);
        double plabel = example.getValue(predictedLabelAttribute);
        if (labelAttribute.isNominal()) {
            String predLabelString = predictedLabelAttribute.getMapping().mapIndex((int) plabel);
            plabel = labelAttribute.getMapping().getIndex(predLabelString);
        }

        double weight = 1.0d;
        if (weightAttribute != null) {
            weight = example.getValue(weightAttribute);
        }

        double prod = label * plabel * weight;
        if (!Double.isNaN(prod)) {
            sumLabelPredict += prod;
            sumLabel += label * weight;
            sumLabelSqr += label * label * weight;
            sumPredict += plabel * weight;
            sumPredictSqr += plabel * plabel * weight;
            exampleCount += weight;
        }
    }

    @Override
    public String getDescription() {
        return "Returns the correlation coefficient between the label and predicted label.";
    }

    @Override
    public double getMikroAverage() {
        double divider = Math.sqrt(exampleCount * sumLabelSqr - sumLabel * sumLabel)
                * Math.sqrt(exampleCount * sumPredictSqr - sumPredict * sumPredict);
        double r = (exampleCount * sumLabelPredict - sumLabel * sumPredict) / divider;
        if (r < 0 || Double.isNaN(r)) {
            return 0; // possible due to rounding errors
        }
        if (r > 1) {
            return 1;
        }
        return r;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    public void startCounting(ExampleSet eset, boolean useExampleWeights) throws OperatorException {
        super.startCounting(eset, useExampleWeights);
        exampleCount = 0;
        sumLabelPredict = sumLabel = sumPredict = sumLabelSqr = sumPredictSqr = 0.0d;
        this.labelAttribute = eset.getAttributes().getLabel();
        this.predictedLabelAttribute = eset.getAttributes().getPredictedLabel();
        if (useExampleWeights) {
            this.weightAttribute = eset.getAttributes().getWeight();
        }
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        CorrelationCriterion other = (CorrelationCriterion) performance;
        this.sumLabelPredict += other.sumLabelPredict;
        this.sumLabelSqr += other.sumLabelSqr;
        this.sumPredictSqr += other.sumPredictSqr;
        this.sumLabel += other.sumLabel;
        this.sumPredict += other.sumPredict;
        this.exampleCount += other.exampleCount;
    }

    @Override
    public double getFitness() {
        return getAverage();
    }

    @Override
    @JsonProperty
    public String getName() {
        return I18N.getMessage("pio.CorrelationCriterion.correlation");
    }
}
