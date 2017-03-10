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
 * The margin of a classifier, defined as the minimal confidence for the correct label.
 */
public class Margin extends MeasuredPerformance {

    private static final long serialVersionUID = -2987795640706342168L;

    /**
     * The value of the criterion.
     */
    @JsonProperty
    private double margin = Double.NaN;

    @JsonProperty
    private double counter = 1.0d;

    /**
     * Clone constructor.
     */
    public Margin() {
    }

    public Margin(Margin m) {
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
        this.margin = 1.0d;
        Attribute labelAttr = exampleSet.getAttributes().getLabel();
        while (reader.hasNext()) {
            Example example = reader.next();
            String trueLabel = example.getNominalValue(labelAttr);
            double confidence = example.getConfidence(trueLabel);
            this.margin = Math.min(margin, confidence);
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
     * Returns the fitness.
     */
    @Override
    public double getFitness() {
        return getAverage();
    }

    @Override
    public String getName() {
        return I18N.getMessage("pio.Margin.margin");
    }

    @Override
    public String getDescription() {
        return "The margin of a classifier, defined as the minimal confidence for the correct label.";
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        Margin other = (Margin) performance;
        this.margin += other.margin;
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
