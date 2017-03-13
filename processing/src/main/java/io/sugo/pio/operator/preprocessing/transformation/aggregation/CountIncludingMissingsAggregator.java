package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;

import java.util.HashSet;


/**
 * This is an {@link Aggregator} for the {@link CountIncludingMissingsAggregationFunction} It counts
 * all examples, if not ignoreMissing is true. Anyway, if only distinct values are counted, Missings
 * are ignored.
 * 
 */
public class CountIncludingMissingsAggregator implements Aggregator {

	private Attribute sourceAttribute;
	private double count = 0;
	private boolean ignoreMissings;

	private boolean isCountingOnlyDistinct = false;
	private HashSet<Double> valuesOccured = null;

	public CountIncludingMissingsAggregator(AggregationFunction function) {
		this.sourceAttribute = function.getSourceAttribute();
		ignoreMissings = function.isIgnoringMissings();
		this.isCountingOnlyDistinct = function.isCountingOnlyDistinct();
		if (isCountingOnlyDistinct) {
			valuesOccured = new HashSet<Double>();
		}

	}

	@Override
	public void count(Example example) {
		if (!ignoreMissings && !isCountingOnlyDistinct) {
			count++;
		} else {
			double value = example.getValue(sourceAttribute);
			if (!Double.isNaN(value)) {
				if (!isCountingOnlyDistinct || valuesOccured.add(value)) {
					count++;
				}
			}
		}
	}

	@Override
	public void count(Example example, double weight) {
		if (!ignoreMissings && !isCountingOnlyDistinct) {
			count += weight;
		} else {
			double value = example.getValue(sourceAttribute);
			if (!Double.isNaN(value)) {
				if (!isCountingOnlyDistinct || valuesOccured.add(value)) {
					count += weight;
				}
			}
		}
	}

	@Override
	public void set(Attribute attribute, DataRow row) {
		row.set(attribute, count);
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}
}
