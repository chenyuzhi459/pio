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
 * Relative squared error is the total squared error made relative to what the error would have been
 * if the prediction had been the average of the absolute value. As done with the root mean-squared
 * error, the square root of the relative squared error is taken to give it the same dimensions as
 * the predicted values themselves. Also, just like root mean-squared error, this exaggerates the
 * cases in which the prediction error was significantly greater than the mean error.
 *
 * @author Ingo Mierswa ingomierswa Exp $
 */
public class RootRelativeSquaredError extends MeasuredPerformance {

    private static final long serialVersionUID = 7781104825149866444L;

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
    private double exampleCounter = 0;

    public RootRelativeSquaredError() {
    }

    public RootRelativeSquaredError(RootRelativeSquaredError rse) {
        super(rse);
        this.deviationSum = rse.deviationSum;
        this.relativeSum = rse.relativeSum;
        this.trueLabelSum = rse.trueLabelSum;
        this.exampleCounter = rse.exampleCounter;
        this.labelAttribute = (Attribute) rse.labelAttribute.clone();
        this.predictedAttribute = (Attribute) rse.predictedAttribute.clone();
        if (rse.weightAttribute != null) {
            this.weightAttribute = (Attribute) rse.weightAttribute.clone();
        }
    }

    @Override
    public String getName() {
        return I18N.getMessage("pio.RootRelativeSquaredError.root_relative_squared_error");
    }

    @Override
    public String getDescription() {
        return "Averaged root-relative-squared error";
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
                    "root relative squared error can only be calculated for test sets with more than 2 examples.");
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
            double label = example.getValue(labelAttribute);
            if (!Double.isNaN(label)) {
                exampleCounter += 1;
                trueLabelSum += label;
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
            String labelS = example.getNominalValue(labelAttribute);
            plabel = example.getConfidence(labelS);
            label = 1.0d;
        }

        double weight = 1.0d;
        if (weightAttribute != null) {
            weight = example.getValue(weightAttribute);
        }

        double diff = Math.abs(label - plabel);
        deviationSum += diff * diff * weight * weight;
        double relDiff = Math.abs(label - (trueLabelSum / exampleCounter));
        relativeSum += relDiff * relDiff * weight * weight;
    }

    @Override
    public double getMikroAverage() {
        return Math.sqrt(deviationSum / relativeSum);
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    public double getFitness() {
        return (-1) * getAverage();
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        RootRelativeSquaredError other = (RootRelativeSquaredError) performance;
        this.deviationSum += other.deviationSum;
        this.relativeSum += other.relativeSum;
    }
}