package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;

import java.util.HashSet;


/**
 * This is an {@link Aggregator} for the {@link CountAggregationFunction} It counts all non-NaN
 * values.
 * 
 */
public class CountIgnoringMissingsAggregator implements Aggregator {

	private Attribute sourceAttribute;
	private double count = 0;

	private boolean isCountingOnlyDistinct = false;
	private HashSet<Double> valuesOccured = null;

	public CountIgnoringMissingsAggregator(AggregationFunction function) {
		this.sourceAttribute = function.getSourceAttribute();
		this.isCountingOnlyDistinct = function.isCountingOnlyDistinct();
		if (isCountingOnlyDistinct) {
			valuesOccured = new HashSet<Double>();
		}
	}

	@Override
	public void count(Example example) {
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			if (!isCountingOnlyDistinct || valuesOccured.add(value)) {
				count++;
			}
		}
	}

	@Override
	public void count(Example example, double weight) {
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			if (!isCountingOnlyDistinct || valuesOccured.add(value)) {
				count += weight;
			}
		}
	}

	@Override
	public void set(Attribute attribute, DataRow row) {
		row.set(attribute, count);
	}
}
