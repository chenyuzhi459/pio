package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Median Aggregation function. This will calculate the median of a source
 * attribute for each group by remembering all values.
 * 
 */
public class MedianAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_MEDIAN = "median";

	public MedianAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_MEDIAN, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public MedianAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new MedianAggregator(this);
	}

	@Override
	protected int getTargetValueType(int sourceValueType) {
		return sourceValueType;
	}

	@Override
	public boolean isCompatible() {
		return getSourceAttribute().isNumerical()
				|| Ontology.ATTRIBUTE_VALUE_TYPE.isA(getSourceAttribute().getValueType(), Ontology.DATE_TIME);
	}

}
