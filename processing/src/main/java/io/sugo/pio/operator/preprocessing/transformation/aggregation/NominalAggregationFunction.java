package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.tools.Ontology;


/**
 * This class implements an abstract superclass for all Nominal aggregation functions, the new
 * attribute will have an empty mapping. All subclasses must take care to modify the mapping
 * accordingly, if adding new nominal values.
 * 
 */
public abstract class NominalAggregationFunction extends AggregationFunction {

	private Attribute targetAttribute;

	public NominalAggregationFunction(Attribute sourceAttribute, boolean ignoreMissings, boolean countOnlyDisctinct,
                                      String functionName, String separatorOpen, String separatorClose) {
		super(sourceAttribute, ignoreMissings, countOnlyDisctinct);
		if (sourceAttribute.isNominal()) {
			this.targetAttribute = AttributeFactory.createAttribute(functionName + separatorOpen
					+ getSourceAttribute().getName() + separatorClose, Ontology.POLYNOMINAL);
		}
	}

	@Override
	public Attribute getTargetAttribute() {
		return targetAttribute;
	}

	@Override
	public final boolean isCompatible() {
		return getSourceAttribute().isNominal();
	}

}
