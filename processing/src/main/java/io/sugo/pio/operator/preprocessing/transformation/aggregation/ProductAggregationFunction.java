package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.DoubleArrayDataRow;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements the Product Aggregation function. This will calculate the product of a
 * source attribute for each group.
 * 
 */
public class ProductAggregationFunction extends NumericalAggregationFunction {

	public static final String FUNCTION_PRODUCT = "product";

	public ProductAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_PRODUCT, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public ProductAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct, functionName, separatorOpen, separatorClose);
	}

	@Override
	public Aggregator createAggregator() {
		return new ProductAggregator(this);
	}

	@Override
	public void setDefault(Attribute attribute, DoubleArrayDataRow row) {
		row.set(attribute, 1);
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
