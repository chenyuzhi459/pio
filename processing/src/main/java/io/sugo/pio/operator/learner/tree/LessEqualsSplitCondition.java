package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

/**
 * A split condition for numerical values (less equals).
 *
 * @author Ingo Mierswa
 */
public class LessEqualsSplitCondition extends AbstractSplitCondition {

	private static final long serialVersionUID = 6658964566718050949L;

	@JsonProperty
	private final double value;
	private final int attValueType;

	public LessEqualsSplitCondition(Attribute attribute, double value) {
		super(attribute.getName());
		this.value = value;
		this.attValueType = attribute.getValueType();
	}

	@Override
	public boolean test(Example example) {
		return example.getValue(example.getAttributes().get(getAttributeName())) <= value;
	}

	@Override
	public String getRelation() {
		return "\u2264";
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
