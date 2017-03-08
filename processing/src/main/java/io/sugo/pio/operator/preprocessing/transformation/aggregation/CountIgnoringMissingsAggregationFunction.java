package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.DoubleArrayDataRow;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Count Aggregation function. This will calculate the number of examples
 * within a group. Examples with a missing value in the attribute won't be counted. To count all of
 * them use the {@link CountIncludingMissingsAggregationFunction}. TODO: Needs to be checked whether
 * this is same as in old operator.
 * 
 */
public class CountIgnoringMissingsAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_COUNT = "countWithOutMissings";

	public CountIgnoringMissingsAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings,
			boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_COUNT, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public CountIgnoringMissingsAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings,
			boolean countOnlyDisctinct, String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new CountAggregator(this);
	}

	@Override
	public boolean isCompatible() {
		return true;
	}

	@Override
	public void setDefault(Attribute attribute, DoubleArrayDataRow row) {
		row.set(attribute, 0);
	}

	@Override
	protected int getTargetValueType(int sourceValueType) {
		return Ontology.INTEGER;
	}

}
