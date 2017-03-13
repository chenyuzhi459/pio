package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link SumAggregationFunction}
 * 
 */
public class ProductAggregator extends NumericalAggregator {

	private double product = 1d;

	public ProductAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value, double weight) {
		product *= Math.pow(value, weight);
	}

	@Override
	public void count(double value) {
		product *= value;
	}

	@Override
	public double getValue() {
		return product;
	}

	@Override
	public void setValue(double value) {
		this.product = value;
	}

}
