package io.sugo.pio.operator.preprocessing.transformation.aggregation;

/**
 * This is an {@link Aggregator} for the {@link MeanAggregationFunction}
 * 
 */
public class MeanAggregator extends NumericalAggregator {

	private double sum = 0;
	private double totalWeight = 0;

	public MeanAggregator(AggregationFunction function) {
		super(function);
	}

	@Override
	public void count(double value) {
		sum += value;
		totalWeight++;
	}

	@Override
	public void count(double value, double weight) {
		sum += value * weight;
		totalWeight += weight;
	}

	@Override
	public double getValue() {
		return sum / totalWeight;
	}

	@Override
	public void setValue(double value) {
		this.sum = value;
	}

}
