package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link SumAggregationFunction}
 * 
 */
public class SumAggregator extends NumericalAggregator {

	private double sum = 0;

	public SumAggregator(SumAggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value, double weight) {
		sum += value * weight;
	}

	@Override
	public void count(double value) {
		sum += value;
	}

	@Override
	public double getValue() {
		return sum;
	}

	@Override
	public void setValue(double value) {
		this.sum = value;
	}
}
