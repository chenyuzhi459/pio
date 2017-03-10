package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;
import io.sugo.pio.tools.math.MathFunctions;

import java.util.Iterator;


/**
 * Calculates the cross-entropy for the predictions of a classifier.
 */
public class CrossEntropy extends MeasuredPerformance {

    private static final long serialVersionUID = 8341971882780129465L;

    /**
     * The value of the criterion.
     */
    @JsonProperty
    private double value = Double.NaN;

    @JsonProperty
    private double counter = 1.0d;

    /**
     * Clone constructor.
     */
    public CrossEntropy() {
    }

    public CrossEntropy(CrossEntropy c) {
        super(c);
        this.value = c.value;
        this.counter = c.counter;
    }

    /**
     * Calculates the margin.
     */
    @Override
    public void startCounting(ExampleSet exampleSet, boolean useExampleWeights) throws OperatorException {
        super.startCounting(exampleSet, useExampleWeights);
        // compute margin
        Iterator<Example> reader = exampleSet.iterator();
        this.value = 0.0d;
        Attribute labelAttr = exampleSet.getAttributes().getLabel();
        Attribute weightAttribute = null;
        if (useExampleWeights) {
            weightAttribute = exampleSet.getAttributes().getWeight();
        }
        while (reader.hasNext()) {
            Example example = reader.next();
            String trueLabel = example.getNominalValue(labelAttr);
            double confidence = example.getConfidence(trueLabel);
            double weight = 1.0d;
            if (weightAttribute != null) {
                weight = example.getValue(weightAttribute);
            }
            this.value -= weight * MathFunctions.ld(confidence);

            this.counter += weight;
        }
    }

    /**
     * Does nothing. Everything is done in {@link #startCounting(ExampleSet, boolean)}.
     */
    @Override
    public void countExample(Example example) {
    }

    @Override
    public double getExampleCount() {
        return counter;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    public double getMikroAverage() {
        return value / counter;
    }

    /**
     * Returns the fitness.
     */
    @Override
    public double getFitness() {
        return -1 * getAverage();
    }

    @Override
    public String getName() {
        return I18N.getMessage("pio.CrossEntropy.cross_entropy");
    }

    @Override
    public String getDescription() {
        return "The cross-entropy of a classifier, defined as the sum over the logarithms of the true label's confidences divided by the number of examples";
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        CrossEntropy other = (CrossEntropy) performance;
        this.value += other.value;
        this.counter += other.counter;
    }

    /**
     * Returns the super class implementation of toString().
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
