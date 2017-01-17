package io.sugo.pio.example;

/**
 * Attribute statistics object for numerical attributes.
 *
 * @author Ingo Mierswa
 */
public class NumericalStatistics implements Statistics {

	private static final long serialVersionUID = -6283236022093847887L;

	private double sum = 0.0d;

	private double squaredSum = 0.0d;

	private int valueCounter = 0;

	public NumericalStatistics() {}

	/** Clone constructor. */
	private NumericalStatistics(NumericalStatistics other) {
		this.sum = other.sum;
		this.squaredSum = other.squaredSum;
		this.valueCounter = other.valueCounter;
	}

	@Override
	public Object clone() {
		return new NumericalStatistics(this);
	}

	@Override
	public void startCounting(Attribute attribute) {
		this.sum = 0.0d;
		this.squaredSum = 0.0d;
		this.valueCounter = 0;
	}

	@Override
	public void count(double value, double weight) {
		if (!Double.isNaN(value)) {
			sum += value;
			squaredSum += value * value;
			valueCounter++;
		}
	}

	@Override
	public boolean handleStatistics(String name) {
		return AVERAGE.equals(name) || VARIANCE.equals(name) || SUM.equals(name);
	}

	@Override
	public double getStatistics(Attribute attribute, String name, String parameter) {
		if (AVERAGE.equals(name)) {
			return this.sum / this.valueCounter;
		} else if (VARIANCE.equals(name)) {
			if (valueCounter <= 1) {
				return 0;
			}
			double variance = (squaredSum - sum * sum / valueCounter) / (valueCounter - 1);
			if (variance < 0) {
				return 0;
			}
			return variance;
		} else if (SUM.equals(name)) {
			return this.sum;
		} else {
			return Double.NaN;
		}
	}
}
