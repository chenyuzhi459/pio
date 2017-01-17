package io.sugo.pio.example;

/**
 * Attribute statistics object for weighted numerical attributes.
 * 
 * @author Ingo Mierswa, Tobias Malbrecht
 */
public class WeightedNumericalStatistics implements Statistics {

	private static final long serialVersionUID = -6283236022093847887L;

	private double sum = 0.0d;

	private double squaredSum = 0.0d;

	private double totalWeight = 0.0d;

	private double count = 0.0d;

	public WeightedNumericalStatistics() {}

	/** Clone constructor. */
	private WeightedNumericalStatistics(WeightedNumericalStatistics other) {
		this.sum = other.sum;
		this.squaredSum = other.squaredSum;
		this.totalWeight = other.totalWeight;
		this.count = other.count;
	}

	@Override
	public Object clone() {
		return new WeightedNumericalStatistics(this);
	}

	@Override
	public void startCounting(Attribute attribute) {
		this.sum = 0.0d;
		this.squaredSum = 0.0d;
		this.totalWeight = 0;
		this.count = 0;
	}

	@Override
	public void count(double value, double weight) {
		if (Double.isNaN(weight)) {
			weight = 1.0d;
		}
		if (!Double.isNaN(value)) {
			sum += (weight * value);
			squaredSum += weight * value * value;
			totalWeight += weight;
			count++;
		}
	}

	@Override
	public boolean handleStatistics(String name) {
		return AVERAGE_WEIGHTED.equals(name) || VARIANCE_WEIGHTED.equals(name) || SUM_WEIGHTED.equals(name);
	}

	@Override
	public double getStatistics(Attribute attribute, String name, String parameter) {
		if (AVERAGE_WEIGHTED.equals(name)) {
			return this.sum / this.totalWeight;
		} else if (VARIANCE_WEIGHTED.equals(name)) {
			if (count <= 1) {
				return 0;
			}
			return (squaredSum - (sum * sum) / totalWeight) / (((count - 1) / count) * totalWeight);
		} else if (SUM_WEIGHTED.equals(name)) {
			return this.sum;
		} else {
			return Double.NaN;
		}
	}
}
