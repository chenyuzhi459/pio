package io.sugo.pio.operator;

/**
 * The possible capabilities for all learners.
 * 
 * @rapidminer.todo replace by Enumeration after change to Java 1.5
 * @author Julien Nioche, Ingo Mierswa
 */
public enum OperatorCapability {

	POLYNOMINAL_ATTRIBUTES("polynominal attributes"), BINOMINAL_ATTRIBUTES("binominal attributes"), NUMERICAL_ATTRIBUTES(
			"numerical attributes"), POLYNOMINAL_LABEL("polynominal label"), BINOMINAL_LABEL("binominal label"), NUMERICAL_LABEL(
			"numerical label"), ONE_CLASS_LABEL("one class label"), NO_LABEL("unlabeled"), UPDATABLE("updatable"), WEIGHTED_EXAMPLES(
			"weighted examples"), FORMULA_PROVIDER("formula provider"), MISSING_VALUES("missing values");

	private String description;

	private OperatorCapability(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}
}
