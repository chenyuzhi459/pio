package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;


/**
 */
public interface Aggregator {

	/**
	 * This will count the given example to the group this {@link Aggregator} belongs to. The
	 * respective attribute will be queried from the {@link AggregationFunction} this was created
	 * by.
	 */
	public void count(Example example);

	/**
	 * This does the same as {@link #count(Example)}, but will take the weight of the current
	 * example into account.
	 */
	public void count(Example example, double weight);

	/**
	 * This will set the result value into the data row onto the position of the given attribute.
	 */
	public void set(Attribute attribute, DataRow row);

}
