package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;


/**
 * This function first behaves like {@link CountAggregationFunction}, but it delivers fractions of
 * the total count instead of absolute values. E.g. {@link SumAggregationFunction} delivers [20, 50,
 * 30] this function would deliver [0.2, 0.5, 0.3]
 * 
 */
public class CountFractionalAggregationFunction extends AbstractCountRatioAggregationFunction {

	public static final String FUNCTION_COUNT_FRACTIONAL = "fractional_count";

	public CountFractionalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_COUNT_FRACTIONAL);
	}

	public CountFractionalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.operator.preprocessing.transformation.aggregation.
	 * AbstractCountRatioAggregationFunction#getRatioFactor()
	 */
	@Override
	public double getRatioFactor() {
		return 1.0;
	}

}
