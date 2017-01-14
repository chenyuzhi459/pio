package io.sugo.pio.parameter.conditions;

import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.UndefinedParameterError;
import org.w3c.dom.Element;


/**
 * This condition checks if a type parameter (category) has a certain value.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class EqualTypeCondition extends ParameterCondition {

	private static final String ELEMENT_POSSIBLE_OPTIONS = "PossibleOptions";
	private static final String ELEMENT_POSSIBLE_OPTION = "PossibleOption";
	private static final String ELEMENT_FULFILLING_OPTION = "FulfillingOption";
	private static final String ELEMENT_FULFILLING_OPTIONS = "FulfillingOptions";

	private int[] fulfillingOptions;
	private String[] possibleOptions;

	public EqualTypeCondition(ParameterHandler handler, String conditionParameter, String[] options,
							  boolean becomeMandatory, int... types) {
		super(handler, conditionParameter, becomeMandatory);
		this.fulfillingOptions = types;
		this.possibleOptions = options;
	}

	@Override
	public boolean isConditionFullfilled() {
		boolean equals = false;
		int isType;
		try {
			isType = parameterHandler.getParameterAsInt(conditionParameter);
		} catch (UndefinedParameterError e) {
			return false;
		}
		for (int type : fulfillingOptions) {
			equals |= isType == type;
		}
		return equals;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (fulfillingOptions.length > 1) {
			builder.append(conditionParameter.replace('_', ' ') + " \u2208 {");
			for (int i = 0; i < fulfillingOptions.length; i++) {
				builder.append(possibleOptions[fulfillingOptions[i]]);
				if (i + 1 < fulfillingOptions.length) {
					builder.append(", ");
				}
			}
			builder.append("}");
		} else if (fulfillingOptions.length > 0) {
			builder.append(conditionParameter.replace('_', ' ') + " = " + possibleOptions[fulfillingOptions[0]]);
		}
		return builder.toString();
	}
}
