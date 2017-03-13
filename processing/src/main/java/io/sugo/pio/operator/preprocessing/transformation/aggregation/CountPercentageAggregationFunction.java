package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;


/**
 * This function first behaves like {@link CountAggregationFunction}, but it delivers percentages of
 * the total count instead of absolute values. E.g. {@link SumAggregationFunction} delivers [2, 5,
 * 3] this function would deliver [20, 50, 30]
 * 
 *
 */
public class CountPercentageAggregationFunction extends AbstractCountRatioAggregationFunction {

	public static final String FUNCTION_COUNT_PERCENTAGE = "percentage_count";

	public CountPercentageAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_COUNT_PERCENTAGE);
	}

	public CountPercentageAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public double getRatioFactor() {
		return 100.0;
	}

}
