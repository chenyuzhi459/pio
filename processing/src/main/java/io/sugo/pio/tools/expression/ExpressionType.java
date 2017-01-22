package io.sugo.pio.tools.expression;

import io.sugo.pio.tools.Ontology;

/**
 * Enum for the type of an {@link Expression}. Knows the {@link Ontology#ATTRIBUTE_VALUE_TYPE}
 * associated to an ExpressionType and the other way around.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public enum ExpressionType {
	STRING(Ontology.NOMINAL), DOUBLE(Ontology.REAL), INTEGER(Ontology.INTEGER), BOOLEAN(Ontology.BINOMINAL), DATE(
			Ontology.DATE_TIME);	// TEXT(Ontology.STRING);

	private int attributeType;

	private ExpressionType(int attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the associated {@link Ontology#ATTRIBUTE_VALUE_TYPE}
	 */
	public int getAttributeType() {
		return attributeType;
	}

	/**
	 * Returns the {@link ExpressionType} associated to the attributeType.
	 *
	 * @param attributeType
	 *            the {@link Ontology#ATTRIBUTE_VALUE_TYPE}
	 * @return the expression type associated to the attributeType
	 */
	public static ExpressionType getExpressionType(int attributeType) {
		switch (attributeType) {
			case Ontology.DATE:
			case Ontology.DATE_TIME:
			case Ontology.TIME:
				return DATE;
			case Ontology.INTEGER:
				return INTEGER;
			case Ontology.REAL:
			case Ontology.NUMERICAL:
				return DOUBLE;
			default:
				return STRING;
		}
	}
}
