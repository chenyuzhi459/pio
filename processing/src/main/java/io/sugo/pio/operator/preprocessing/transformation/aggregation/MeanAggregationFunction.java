package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Mean Aggregation function. This will calculate the mean of a source
 * attribute for each group.
 * 
 */
public class MeanAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_AVERAGE = "average";

	public MeanAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_AVERAGE, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public MeanAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new MeanAggregator(this);
	}

	@Override
	protected int getTargetValueType(int sourceValueType) {
		return Ontology.ATTRIBUTE_VALUE_TYPE.isA(sourceValueType, Ontology.DATE_TIME) ? Ontology.DATE_TIME : Ontology.REAL;
	}

	@Override
	public boolean isCompatible() {
		return getSourceAttribute().isNumerical()
				|| Ontology.ATTRIBUTE_VALUE_TYPE.isA(getSourceAttribute().getValueType(), Ontology.DATE_TIME);
	}

}
