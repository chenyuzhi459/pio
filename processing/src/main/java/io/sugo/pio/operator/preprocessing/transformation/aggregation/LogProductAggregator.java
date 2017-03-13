package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link LogProductAggregationFunction}
 * 
 */
public class LogProductAggregator extends NumericalAggregator {

	private double logSum = 0;

	public LogProductAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value) {
		logSum += Math.log(value);
	}

	@Override
	protected void count(double value, double weight) {
		logSum += weight * Math.log(value);
	}

	@Override
	public double getValue() {
		return logSum;
	}

	@Override
	public void setValue(double value) {
		this.logSum = value;
	}
}
