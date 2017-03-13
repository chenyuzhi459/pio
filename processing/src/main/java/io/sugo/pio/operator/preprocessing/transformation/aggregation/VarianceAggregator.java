package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link VarianceAggregationFunction}
 * 
 */
public class VarianceAggregator extends NumericalAggregator {

	private double valueSum = 0d;
	private double squaredValueSum = 0d;
	private double totalWeightSum = 0d;
	private double count = 0;

	public VarianceAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value) {
		valueSum += value;
		squaredValueSum += value * value;
		totalWeightSum++;
		count++;
	}

	@Override
	public void count(double value, double weight) {
		valueSum += weight * value;
		squaredValueSum += weight * value * value;
		totalWeightSum += weight;
		count++;
	}

	@Override
	public double getValue() {
		if (count > 0) {
			return (squaredValueSum - valueSum * valueSum / totalWeightSum) / ((count - 1) / count * totalWeightSum);
		} else {
			return Double.NaN;
		}
	}
}
