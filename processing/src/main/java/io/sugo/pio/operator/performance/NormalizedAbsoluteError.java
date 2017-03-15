package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.tools.math.Averagable;

import java.util.Iterator;


/**
 * Normalized absolute error is the total absolute error normalized by the error simply predicting
 * the average of the actual values.
 */
public class NormalizedAbsoluteError extends MeasuredPerformance {

    private static final long serialVersionUID = -3899005486051589953L;

    private Attribute predictedAttribute;

    private Attribute labelAttribute;

    private Attribute weightAttribute;

    @JsonProperty
    private double deviationSum = 0.0d;

    @JsonProperty
    private double relativeSum = 0.0d;

    @JsonProperty
    private double trueLabelSum = 0.0d;

    @JsonProperty
    private double exampleCounter = 0.0d;

    public NormalizedAbsoluteError() {
    }

    public NormalizedAbsoluteError(NormalizedAbsoluteError nae) {
        super(nae);
        this.deviationSum = nae.deviationSum;
        this.relativeSum = nae.relativeSum;
        this.trueLabelSum = nae.trueLabelSum;
        this.exampleCounter = nae.exampleCounter;
        this.labelAttribute = (Attribute) nae.labelAttribute.clone();
        this.predictedAttribute = (Attribute) nae.predictedAttribute.clone();
        if (nae.weightAttribute != null) {
            this.weightAttribute = (Attribute) nae.weightAttribute.clone();
        }
    }

    @Override
    @JsonProperty
    public String getName() {
        return I18N.getMessage("pio.NormalizedAbsoluteError.normalized_absolute_error");
    }

    @Override
    public String getDescription() {
        return "The absolute error divided by the error made if the average would have been predicted.";
    }

    @Override
    public double getExampleCount() {
        return exampleCounter;
    }

    @Override
    public void startCounting(ExampleSet exampleSet, boolean useExampleWeights) throws OperatorException {
        super.startCounting(exampleSet, useExampleWeights);
        if (exampleSet.size() <= 1) {
            throw new UserError(null, "pio.error.operator.cannot_calculate_performance", getName(),
                    "normalized absolute error can only be calculated for test sets with more than 2 examples.");
        }
        this.predictedAttribute = exampleSet.getAttributes().getPredictedLabel();
        this.labelAttribute = exampleSet.getAttributes().getLabel();
        if (useExampleWeights) {
            this.weightAttribute = exampleSet.getAttributes().getWeight();
        }
        this.trueLabelSum = 0.0d;
        this.deviationSum = 0.0d;
        this.relativeSum = 0.0d;
        this.exampleCounter = 0.0d;
        Iterator<Example> reader = exampleSet.iterator();
        while (reader.hasNext()) {
            Example example = reader.next();
            double label = example.getLabel();
            double weight = 1.0d;
            if (weightAttribute != null) {
                weight = example.getValue(weightAttribute);
            }
            if (!Double.isNaN(label)) {
                exampleCounter += weight;
                trueLabelSum += label * weight;
            }
        }
    }

    /**
     * Calculates the error for the current example.
     */
    @Override
    public void countExample(Example example) {
        double plabel;
        double label = example.getValue(labelAttribute);

        if (!predictedAttribute.isNominal()) {
            plabel = example.getValue(predictedAttribute);
        } else {
            String labelS = example.getValueAsString(labelAttribute);
            plabel = example.getConfidence(labelS);
            label = 1.0d;
        }

        double weight = 1.0d;
        if (weightAttribute != null) {
            weight = example.getValue(weightAttribute);
        }

        double diff = weight * Math.abs(label - plabel);
        deviationSum += diff;
        double relDiff = Math.abs(weight * label - (trueLabelSum / exampleCounter));
        relativeSum += relDiff;
    }

    @Override
    public double getMikroAverage() {
        return deviationSum / relativeSum;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    public double getFitness() {
        return -1 * getAverage();
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        NormalizedAbsoluteError other = (NormalizedAbsoluteError) performance;
        this.deviationSum += other.deviationSum;
        this.relativeSum += other.relativeSum;
    }
}
