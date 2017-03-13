package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.DoubleArrayDataRow;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Log Product Aggregation function. This will calculate the logarithm of
 * a product of a source attribute for each group. This can help in situations, where the normal
 * product would exceed the numerical range and should be used as an intermediate result.
 * 
 * This obviously only works on numbers being all greater 0.
 * 
 */
public class LogProductAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_LOG_PRODUCT = "logProduct";

	public LogProductAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_LOG_PRODUCT, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public LogProductAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new LogProductAggregator(this);
	}

	@Override
	public void setDefault(Attribute attribute, DoubleArrayDataRow row) {
		row.set(attribute, 0);
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
