package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;

import java.util.Iterator;


/**
 * The soft margin loss of a classifier, defined as the average over all 1 - y * f(x).
 */
public class SoftMarginLoss extends MeasuredPerformance {

    private static final long serialVersionUID = -2987795640706342168L;

    /**
     * The value of the margin.
     */
    @JsonProperty
    private double margin = Double.NaN;

    /**
     * A counter for average building.
     */
    @JsonProperty
    private double counter = 1;

    /**
     * Clone constructor.
     */
    public SoftMarginLoss() {
    }

    public SoftMarginLoss(SoftMarginLoss m) {
        super(m);
        this.margin = m.margin;
        this.counter = m.counter;
    }

    /**
     * Calculates the margin.
     */
    @Override
    public void startCounting(ExampleSet exampleSet, boolean useExampleWeights) throws OperatorException {
        super.startCounting(exampleSet, useExampleWeights);
        // compute margin
        Iterator<Example> reader = exampleSet.iterator();
        this.margin = 0.0d;
        this.counter = 0.0d;
        Attribute labelAttr = exampleSet.getAttributes().getLabel();
        Attribute weightAttribute = null;
        if (useExampleWeights) {
            weightAttribute = exampleSet.getAttributes().getWeight();
        }
        while (reader.hasNext()) {
            Example example = reader.next();
            String trueLabel = example.getNominalValue(labelAttr);
            double confidence = example.getConfidence(trueLabel);
            double currentMargin = Math.max(0, 1.0d - confidence);

            double weight = 1.0d;
            if (weightAttribute != null) {
                weight = example.getValue(weightAttribute);
            }
            this.margin += currentMargin * weight;

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
        return margin / counter;
    }

    /**
     * Returns 0.
     */
    @Override
    public double getMaxFitness() {
        return 0.0d;
    }

    /**
     * Returns the fitness.
     */
    @Override
    public double getFitness() {
        return -1 * getAverage();
    }

    @Override
    @JsonProperty
    public String getName() {
        return I18N.getMessage("pio.SoftMarginLoss.soft_margin_loss");
    }

    @Override
    public String getDescription() {
        return "The average soft margin loss of a classifier, defined as the average of all 1 - confidences for the correct label.";
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        SoftMarginLoss other = (SoftMarginLoss) performance;
        this.counter += other.counter;
        this.margin += other.margin;
    }

    /**
     * Returns the super class implementation of toString().
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
