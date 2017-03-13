package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;

import java.util.HashSet;


/**
 * This is an implementation of a Aggregator for numerical attributes. It takes over the handling of
 * missing values.
 * 
 */
public abstract class NumericalAggregator implements Aggregator {

	private Attribute sourceAttribute;
	private boolean ignoreMissings;
	private boolean isMissing = false;
	private boolean isCountingOnlyDistinct = false;
	private HashSet<Double> distinctValueSet = null;

	public NumericalAggregator(AggregationFunction function) {
		this.sourceAttribute = function.getSourceAttribute();
		this.ignoreMissings = function.isIgnoringMissings();
		this.isCountingOnlyDistinct = function.isCountingOnlyDistinct();
		if (isCountingOnlyDistinct) {
			distinctValueSet = new HashSet<Double>();
		}
	}

	@Override
	public final void count(Example example) {
		// check whether we have to count at all
		if (!isMissing || ignoreMissings) {
			double value = example.getValue(sourceAttribute);
			if (isMissing && !ignoreMissings || Double.isNaN(value)) {
				isMissing = true;
			} else {
				if (!isCountingOnlyDistinct || distinctValueSet.add(value)) {
					count(value);
				}
			}
		}
	}

	@Override
	public final void count(Example example, double weight) {
		// check whether we have to count at all
		if (!isMissing || ignoreMissings) {
			double value = example.getValue(sourceAttribute);
			if (isMissing && !ignoreMissings || Double.isNaN(value)) {
				isMissing = true;
			} else {
				if (!isCountingOnlyDistinct || distinctValueSet.add(value)) {
					count(value, weight);
				}
			}
		}
	}

	/**
	 * This method will count the given numerical value. This method will not be called in cases,
	 * where the examples value for the given source Attribute is unknown. Subclasses of this class
	 * will in this cases return either NaN if ignoreMissings is false, or will return the value as
	 * if the examples with the missing aren't present at all.
	 * 
	 * Please see {@link #count(double, double)} for taking weights into account. You may not mix
	 * both methods within one aggregation run, as subclasses might implement more memory efficient
	 * data structures when not using weights.
	 */
	protected abstract void count(double value);

	/**
	 * Same as {@link #count(double)}, but taking the weight into account. You may not mix both
	 * methods within one aggregation run, as subclasses might implement more memory efficient data
	 * structures when not using weights.
	 */
	protected abstract void count(double value, double weight);

	@Override
	public final void set(Attribute attribute, DataRow row) {
		if (isMissing && !ignoreMissings) {
			row.set(attribute, Double.NaN);
		} else {
			row.set(attribute, getValue());
		}
	}

	/**
	 * This method has to return the numerical value of this aggregator.
	 */
	protected abstract double getValue();

	/**
	 * Explicitly sets the value of this aggregator. The only place where it makes sense to use this
	 * function is in {@link AggregationFunction#postProcessing(java.util.List)}.
	 * 
	 * The default implementation does nothing.
	 * 
	 * @param value
	 */
	protected void setValue(double value) {
		// do nothing
	}
}
