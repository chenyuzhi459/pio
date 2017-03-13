package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;

import java.util.List;


/**
 * This function first behaves like {@link SumAggregationFunction}, but it delivers fractions of the
 * total sum instead of absolute values. E.g. {@link SumAggregationFunction} delivers [6, 10, 4]
 * this function would deliver [0.3, 0.5, 0.2]
 * 
 *
 */
public class SumFractionalAggregationFunction extends SumAggregationFunction {

	public static final String FUNCTION_SUM_FRACTIONAL = "fractional_sum";

	public SumFractionalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_SUM_FRACTIONAL, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public SumFractionalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
                                            String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public void postProcessing(List<Aggregator> allAggregators) {
		double totalSum = 0;

		// calculate total sum
		for (Aggregator aggregator : allAggregators) {
			double value = ((SumAggregator) aggregator).getValue();
			if (value < 0 || Double.isNaN(value)) {
				totalSum = Double.NaN;
				break;
			}
			totalSum += value;
		}

		// devide by total sum
		for (Aggregator aggregator : allAggregators) {
			SumAggregator sumAggregator = (SumAggregator) aggregator;
			sumAggregator.setValue(sumAggregator.getValue() / totalSum);
		}
	}
}
