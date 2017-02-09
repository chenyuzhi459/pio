package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;

/**
 * A split condition for numerical missing values (equals).
 *
 * @author Gisa Schaefer
 */
public class NumericalMissingSplitCondition extends AbstractSplitCondition {

	private static final long serialVersionUID = 3883155435836330171L;

	/** the symbol displayed in the decision tree for numerical missing values */
	private static final String SYMBOL_MISSING_NUMERICAL = "?";

	public NumericalMissingSplitCondition(Attribute attribute) {
		super(attribute.getName());
	}

	@Override
	public boolean test(Example example) {
		double currentValue = example.getValue(example.getAttributes().get(getAttributeName()));
		return Double.isNaN(currentValue);
	}

	@Override
	public String getRelation() {
		return "=";
	}

	@Override
	public String getValueString() {
		return SYMBOL_MISSING_NUMERICAL;
	}
}
