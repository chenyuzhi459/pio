package io.sugo.pio.operator.performance;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;

import java.util.logging.Level;


/**
 * Simple criteria are those which error can be counted for each example and can be averaged by the
 * number of examples. Since errors should be minimized, the fitness is calculated as -1 multiplied
 * by the the error. Subclasses might also want to implement the method
 * <code>transform(double)</code> which applies a transformation on the value sum divided by the
 * number of counted examples. This is for example usefull in case of root_means_squared error. All
 * subclasses can be used for both regression and classification problems. In case of classification
 * the confidence value for the desired true label is used as prediction.
 * 
 * @author Ingo Mierswa, Simon Fischer
 */
public abstract class SimpleCriterion extends MeasuredPerformance {

	private static final long serialVersionUID = 242287213804685323L;

	private double sum = 0.0;

	private double squaresSum = 0.0;

	private double exampleCount = 0;

	private Attribute predictedAttribute;

	private Attribute labelAttribute;

	private Attribute weightAttribute;

	public SimpleCriterion() {}

	public SimpleCriterion(SimpleCriterion sc) {
		super(sc);
		this.sum = sc.sum;
		this.squaresSum = sc.squaresSum;
		this.exampleCount = sc.exampleCount;
		this.labelAttribute = (Attribute) sc.labelAttribute.clone();
		this.predictedAttribute = (Attribute) sc.predictedAttribute.clone();
		if (sc.weightAttribute != null) {
			this.weightAttribute = (Attribute) sc.weightAttribute.clone();
		}
	}

	@Override
	public double getExampleCount() {
		return exampleCount;
	}

	/**
	 * Invokes <code>countExample(double, double)</code> and counts the deviation. In case of a
	 * nominal label the confidence of the desired true label is used as prediction. For regression
	 * problems the usual predicted label is used.
	 */
	@Override
	public void countExample(Example example) {
		double plabel;
		double label = example.getValue(labelAttribute);
		double weight = 1.0d;
		if (weightAttribute != null) {
			weight = example.getValue(weightAttribute);
		}
		if (!predictedAttribute.isNominal()) {
			plabel = example.getValue(predictedAttribute);
		} else {
			String labelS = example.getNominalValue(labelAttribute);
			plabel = example.getConfidence(labelS);
			label = 1.0d;
		}

		double deviation = countExample(label, plabel);
		if (!Double.isNaN(deviation)) {
			countExampleWithWeight(deviation, weight);
		}
	}

	/** Subclasses must count the example and return the value to sum up. */
	protected abstract double countExample(double label, double predictedLabel);

	/**
	 * Simply returns the given value. Subclasses might apply a transformation on the error sum
	 * divided by the number of examples.
	 */
	protected double transform(double value) {
		return value;
	}

	protected void countExampleWithWeight(double deviation, double weight) {
		if (!Double.isNaN(deviation)) {
			sum += deviation * weight;
			squaresSum += deviation * deviation * weight * weight;
			exampleCount += weight;
		}
	}

	@Override
	public double getMikroAverage() {
		return transform(sum / exampleCount);
	}

	@Override
	public double getMikroVariance() {
		double mean = getMikroAverage();
		double meanSquares = transform(squaresSum) / exampleCount;
		return meanSquares - mean * mean;
	}

	@Override
	public void startCounting(ExampleSet eset, boolean useExampleWeights) throws OperatorException {
		super.startCounting(eset, useExampleWeights);
		exampleCount = 0.0d;
		sum = squaresSum = 0.0d;
		this.predictedAttribute = eset.getAttributes().getPredictedLabel();
		this.labelAttribute = eset.getAttributes().getLabel();
		if (useExampleWeights) {
			this.weightAttribute = eset.getAttributes().getWeight();
		}
	}

	@Override
	public double getFitness() {
		return (-1.0d) * getAverage();
	}

	/** Returns 0.0. */
	@Override
	public double getMaxFitness() {
		return 0.0d;
	}

	@Override
	public void buildSingleAverage(Averagable performance) {
		SimpleCriterion other = (SimpleCriterion) performance;
		this.sum += other.sum;
		this.squaresSum += other.squaresSum;
		this.exampleCount += other.exampleCount;
	}
}
