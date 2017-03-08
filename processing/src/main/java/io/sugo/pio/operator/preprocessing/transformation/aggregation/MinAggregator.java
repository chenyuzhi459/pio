package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link MaxAggregationFunction}
 * 
 */
public class MinAggregator extends NumericalAggregator {

	private double min = Double.POSITIVE_INFINITY;
	private boolean hasValue = false;

	public MinAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value) {
		hasValue = true;
		if (min > value) {  // NaN would always return false: Implicit NaN check
			min = value;
		}
	}

	@Override
	public void count(double value, double weight) {
		hasValue = true;
		if (min > value) {  // NaN would always return false: Implicit NaN check
			min = value;
		}
	}

	@Override
	public double getValue() {
		if (hasValue) {
			return min;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public void setValue(double value) {
		this.min = value;
	}
}
