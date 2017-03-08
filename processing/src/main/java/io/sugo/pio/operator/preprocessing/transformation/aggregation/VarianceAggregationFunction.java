package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Variance Aggregation function. This will calculate the variance of a
 * source attribute for each group.
 * 
 */
public class VarianceAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_VARIANCE = "variance";

	public VarianceAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_VARIANCE, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public VarianceAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new VarianceAggregator(this);
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
