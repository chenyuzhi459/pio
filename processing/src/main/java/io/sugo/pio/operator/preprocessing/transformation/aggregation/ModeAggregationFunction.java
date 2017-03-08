package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.NominalMapping;


/**
 * This class implements the Mode Aggregation function. This will calculate the mode value of the
 * attribute of the examples within a group.
 * 
 */
public class ModeAggregationFunction extends AggregationFunction {

	public static final String FUNCTION_MODE = "mode";
	private Attribute targetAttribute;

	public ModeAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct) {
		this(sourceAttribute, ignoreMissings, countOnlyDisctinct, FUNCTION_MODE, FUNCTION_SEPARATOR_OPEN,
				FUNCTION_SEPARATOR_CLOSE);
	}

	public ModeAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
			String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct);
		this.targetAttribute = AttributeFactory.createAttribute(FUNCTION_MODE + FUNCTION_SEPARATOR_OPEN
				+ getSourceAttribute().getName() + FUNCTION_SEPARATOR_CLOSE, getSourceAttribute().getValueType());
		if (sourceAttribute.isNominal()) {
			this.targetAttribute.setMapping((NominalMapping) sourceAttribute.getMapping().clone());
		}

	}

	@Override
	public Attribute getTargetAttribute() {
		return targetAttribute;
	}

	@Override
	public boolean isCompatible() {
		return getSourceAttribute().isNominal() || getSourceAttribute().isNumerical(); // Both must
																						// be
																						// supported
																						// for
																						// backward
																						// compatibility
	}

	@Override
	public Aggregator createAggregator() {
		return new ModeAggregator(this);
	}
}
