package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;


/**
 * This class implements the Mode Aggregation function. This will calculate the least value of the
 * attribute of the examples within a group, that at least occurrs once.
 * 
 */
public class LeastOccurringAggregationFunction extends NominalAggregationFunction {

	public static final String FUNCTION_LEAST_OCCURRING = "leastO";

	public LeastOccurringAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_LEAST_OCCURRING, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public LeastOccurringAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new LeastOccurringAggregator(this);
	}
}
