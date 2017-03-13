package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link MaxAggregationFunction}
 * 
 */
public class MaxAggregator extends NumericalAggregator {

	private double max = Double.NEGATIVE_INFINITY;
	private boolean hasValue = false;

	public MaxAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value) {
		hasValue = true;
		if (max < value) {  // NaN would always return false: Implicit NaN check
			max = value;
		}
	}

	@Override
	protected void count(double value, double weight) {
		hasValue = true;
		if (max < value) {  // NaN would always return false: Implicit NaN check
			max = value;
		}
	}

	@Override
	public double getValue() {
		if (hasValue) {
			return max;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public void setValue(double value) {
		this.max = value;
	}
}
