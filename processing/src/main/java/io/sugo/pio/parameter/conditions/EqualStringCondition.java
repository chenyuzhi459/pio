package io.sugo.pio.parameter.conditions;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.UndefinedParameterError;

/**
 * This condition checks if a string parameter (also string category) has a certain value.
 * 
 * @author Ingo Mierswa
 */
public class EqualStringCondition extends ParameterCondition {

	private static final String ELEMENT_VALUES = "Values";
	private static final String ELEMENT_VALUE = "Value";

	@JsonProperty
	private String[] types;

	public EqualStringCondition(ParameterHandler handler, String conditionParameter, boolean becomeMandatory,
								String... types) {
		super(handler, conditionParameter, becomeMandatory);
		this.types = types;
	}

	@Override
	public boolean isConditionFullfilled() {
		boolean equals = false;
		String isType;
		try {
			isType = parameterHandler.getParameterAsString(conditionParameter);
		} catch (UndefinedParameterError e) {
			return false;
		}
		for (String type : types) {
			equals |= type.equals(isType);
		}
		return equals;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (types.length > 1) {
			builder.append(conditionParameter.replace('_', ' ') + " \u2208 {");
			for (int i = 0; i < types.length; i++) {
				builder.append(types[i]);
				if (i + 1 < types.length) {
					builder.append(", ");
				}
			}
			builder.append("}");
		} else {
			if (types.length > 0) {
				builder.append(conditionParameter.replace('_', ' ') + " = " + types[0]);
			}
		}
		return builder.toString();
	}
}
