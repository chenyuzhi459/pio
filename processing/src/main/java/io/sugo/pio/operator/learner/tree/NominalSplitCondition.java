package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.tools.Tools;

/**
 * A split condition for nominal values (equals).
 *
 * @author Ingo Mierswa
 */
public class NominalSplitCondition extends AbstractSplitCondition {

	private static final long serialVersionUID = 3883155435836330171L;

	private double value;

	private String valueString;

	public NominalSplitCondition(Attribute attribute, String valueString) {
		super(attribute.getName());
		if (valueString == null) {
			this.value = Double.NaN;
			this.valueString = Attribute.MISSING_NOMINAL_VALUE;
		} else {
			this.value = attribute.getMapping().getIndex(valueString);
			this.valueString = valueString;
		}
	}

	@Override
	public boolean test(Example example) {
		double currentValue = example.getValue(example.getAttributes().get(getAttributeName()));
		return Tools.isEqual(currentValue, value);
	}

	@Override
	public String getRelation() {
		return "=";
	}

	@Override
	public String getValueString() {
		return this.valueString;
	}
}
