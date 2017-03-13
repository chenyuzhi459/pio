package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.AttributeFactory;


/**
 * This class implements the Sum Aggregation function. This will calculate the sum of a source
 * attribute for each group.
 * 
 */
public abstract class NumericalAggregationFunction extends AggregationFunction {

	private Attribute targetAttribute;

	public NumericalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct);
		this.targetAttribute = AttributeFactory.createAttribute(functionName + separatorOpen
				+ getSourceAttribute().getName() + separatorClose, getTargetValueType(sourceAttribute.getValueType()));
	}

	/**
	 * Returns the attribute type to assign to the created {@link #targetAttribute} given the value
	 * type of the source attribute.
	 */
	protected abstract int getTargetValueType(int sourceValueType);

	@Override
	public Attribute getTargetAttribute() {
		return targetAttribute;
	}

}
