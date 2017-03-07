package io.sugo.pio.operator.performance;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;


/**
 * Returns the average value of the prediction. This criterion can be used to detect whether a
 * learning scheme predicts nonsense, e.g. always make the same error. This criterion is not
 * suitable for evaluating the performance and should never be used as main criterion. The
 * {@link #getFitness()} method always returns 0.
 * 
 */
public class PredictionAverage extends MeasuredPerformance {

	private static final long serialVersionUID = -5316112625406102611L;

	private double sum;

	private double squaredSum;

	private double count;

	private Attribute labelAttribute;

	private Attribute weightAttribute;

	public PredictionAverage() {}

	public PredictionAverage(PredictionAverage pa) {
		super(pa);
		this.sum = pa.sum;
		this.squaredSum = pa.squaredSum;
		this.count = pa.count;
		this.labelAttribute = (Attribute) pa.labelAttribute.clone();
		if (pa.weightAttribute != null) {
			this.weightAttribute = (Attribute) pa.weightAttribute.clone();
		}
	}

	@Override
	public double getExampleCount() {
		return count;
	}

	@Override
	public void countExample(Example example) {
		double weight = 1.0d;
		if (weightAttribute != null) {
			weight = example.getValue(weightAttribute);
		}
		count += weight;
		double v = example.getLabel();
		if (!Double.isNaN(v)) {
			sum += v * weight;
			squaredSum += v * v * weight * weight;
		}
	}

	@Override
	public double getMikroAverage() {
		return sum / count;
	}

	@Override
	public double getMikroVariance() {
		double avg = getMikroAverage();
		return (squaredSum / count) - avg * avg;
	}

	@Override
	public void startCounting(ExampleSet set, boolean useExampleWeights) throws OperatorException {
		super.startCounting(set, useExampleWeights);
		count = 0;
		sum = 0.0;
		this.labelAttribute = set.getAttributes().getLabel();
		if (useExampleWeights) {
			this.weightAttribute = set.getAttributes().getWeight();
		}
	}

	@Override
	public String getName() {
		return I18N.getMessage("pio.PredictionAverage.prediction_average");
	}

	/** Returns 0. */
	@Override
	public double getFitness() {
		return 0.0;
	}

	@Override
	public void buildSingleAverage(Averagable performance) {
		PredictionAverage other = (PredictionAverage) performance;
		this.sum += other.sum;
		this.squaredSum += other.squaredSum;
		this.count += other.count;
	}

	@Override
	public String getDescription() {
		return "This is not a real performance measure, but merely the average of the predicted labels.";
	}
}
