package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.tools.Ontology;

import java.util.List;


/**
 * This function first behaves like {@link CountAggregationFunction}, but it delivers percentages of
 * the total count instead of absolute values. E.g. {@link SumAggregationFunction} delivers [2, 5,
 * 3] this function would deliver [20, 50, 30]
 * 
 */
public abstract class AbstractCountRatioAggregationFunction extends CountAggregationFunction {

	public static final String FUNCTION_COUNT_PERCENTAGE = "percentage_count";

	public AbstractCountRatioAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings,
                                                 boolean countOnlyDisctinct, String functionName) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public AbstractCountRatioAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings,
                                                 boolean countOnlyDisctinct, String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	public abstract double getRatioFactor();

	@Override
	public Aggregator createAggregator() {
		return new CountIncludingMissingsAggregator(this);
	}

	@Override
	public void postProcessing(List<Aggregator> allAggregators) {
		double totalCount = 0;

		// calculate total count
		for (Aggregator aggregator : allAggregators) {
			double value = ((CountIncludingMissingsAggregator) aggregator).getCount();
			if (Double.isNaN(value)) {
				totalCount = Double.NaN;
				break;
			}
			totalCount += value;
		}

		// devide by total count
		for (Aggregator aggregator : allAggregators) {
			CountIncludingMissingsAggregator countAggregator = (CountIncludingMissingsAggregator) aggregator;
			countAggregator.setCount((countAggregator.getCount() / totalCount) * getRatioFactor());
		}
	}

	@Override
	protected int getTargetValueType(int sourceValueType) {
		return Ontology.REAL;
	}
}
