package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;


/**
 * This is an {@link Aggregator} for the {@link LeastAggregationFunction}. If the least value is not
 * unique, the first value from the nominal mapping will be used.
 * 
 */
public class LeastAggregator implements Aggregator {

	private Attribute sourceAttribute;
	private double[] frequencies;

	public LeastAggregator(AggregationFunction function) {
		this.sourceAttribute = function.getSourceAttribute();
		frequencies = new double[sourceAttribute.getMapping().size()];
	}

	@Override
	public void count(Example example) {
		count(example, 1d);
	}

	@Override
	public void count(Example example, double weight) {
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			frequencies[(int) value] += weight;
		}
	}

	@Override
	public void set(Attribute attribute, DataRow row) {
		int minIndex = -1;
		double minFrequency = Double.POSITIVE_INFINITY;
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] < minFrequency) {
				minIndex = i;
				minFrequency = frequencies[i];
			}
		}
		// if any counter was greater 0, set result to maximum
		if (minIndex > -1) {
			row.set(attribute, attribute.getMapping().mapString(sourceAttribute.getMapping().mapIndex(minIndex)));
		} else {
			row.set(attribute, Double.NaN);
		}
	}
}
