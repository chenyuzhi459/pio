package io.sugo.pio.tools.expression;


import io.sugo.pio.tools.Ontology;

/**
 * This is an enumeration for possible unknown values. This is used to determine, of which type an
 * returned unknown actually is.
 * 
 * @author Sebastian Land
 */
public enum UnknownValue {
	UNKNOWN_NOMINAL(Ontology.NOMINAL),
	// UNKNOWN_NUMERICAL(Ontology.NUMERICAL), Numerical Unknowns must be encoded by Double.NaN
	UNKNOWN_BOOLEAN(Ontology.BINOMINAL), UNKNOWN_DATE(Ontology.DATE_TIME);

	private int valueType;

	UnknownValue(int valueType) {
		this.valueType = valueType;
	}

	public int getValueType() {
		return valueType;
	}
}
