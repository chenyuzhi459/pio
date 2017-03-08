package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;


/**
 */
public class ConcatAggregator implements Aggregator {

	private ConcatAggregationFunction function;
	boolean first = true;
	private StringBuilder concatenation = new StringBuilder();

	public ConcatAggregator(ConcatAggregationFunction concatAggregationFunction) {
		this.function = concatAggregationFunction;
	}

	@Override
	public void count(Example example) {

		Attribute sourceAttribute = function.getSourceAttribute();
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			if (first) {
				first = false;
			} else {
				concatenation.append(function.getSeparator());
			}
			String nominalValue = sourceAttribute.getMapping().mapIndex((int) value);
			concatenation.append(nominalValue);
		}
	}

	@Override
	public void count(Example example, double weight) {
		count(example);
	}

	@Override
	public void set(Attribute attribute, DataRow row) {
		int idx = attribute.getMapping().mapString(concatenation.toString());
		attribute.setValue(row, idx);
	}

}
