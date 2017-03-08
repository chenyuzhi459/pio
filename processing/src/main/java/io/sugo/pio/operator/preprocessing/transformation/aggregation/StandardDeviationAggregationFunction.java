package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Standard Deviation Aggregation function. This will calculate the
 * standard deviation of a source attribute for each group.
 * 
 */
public class StandardDeviationAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_STANDARD_DEVIATION = "standard_deviation";

	public StandardDeviationAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_STANDARD_DEVIATION, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public StandardDeviationAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings,
			boolean countOnlyDisctinct, String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new StandardDeviationAggregator(this);
	}

	@Override
	protected int getTargetValueType(int sourceValueType) {
		return Ontology.REAL;
	}

	@Override
	public boolean isCompatible() {
		return getSourceAttribute().isNumerical();
	}

}
