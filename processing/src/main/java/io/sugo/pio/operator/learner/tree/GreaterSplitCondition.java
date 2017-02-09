package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

/**
 * Returns true if the value of the desired attribute is greater then a given threshold.
 *
 * @author Ingo Mierswa
 */
public class GreaterSplitCondition extends AbstractSplitCondition {

	private static final long serialVersionUID = 7094464803196955502L;

	@JsonProperty
	private double value;
	private final int attValueType;

	public GreaterSplitCondition(Attribute attribute, double value) {
		super(attribute.getName());
		this.value = value;
		this.attValueType = attribute.getValueType();
	}

	@Override
	public boolean test(Example example) {
		return example.getValue(example.getAttributes().get(getAttributeName())) > value;
	}

	@Override
	public String getRelation() {
		return ">";
	}

	public double getValue() {
		return value;
	}

	@Override
	public String getValueString() {
		if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attValueType, Ontology.DATE)) {
			return Tools.createDateAndFormat(value);
		} else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attValueType, Ontology.TIME)) {
			return Tools.createTimeAndFormat(value);
		} else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attValueType, Ontology.DATE_TIME)) {
			return Tools.createDateTimeAndFormat(value);
		} else {
			return Tools.formatIntegerIfPossible(this.value);
		}
	}
}
