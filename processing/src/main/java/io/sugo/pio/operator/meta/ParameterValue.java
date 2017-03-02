package io.sugo.pio.operator.meta;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.Operator;

import java.io.Serializable;
import java.util.Map;

/**
 * The parameter values used by the class {@link ParameterSet}.
 * 
 * @author Ingo Mierswa
 */
public class ParameterValue implements Serializable {

	private static final long serialVersionUID = -6847818423564185071L;

	private final String operator;

	private final String parameterKey;

	private final String parameterValue;

	public ParameterValue(String operator, String parameterKey, String parameterValue) {
		this.operator = operator;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
	}

	public String getOperator() {
		return operator;
	}

	public String getParameterKey() {
		return parameterKey;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	@Override
	public String toString() {
		return operator + "." + parameterKey + "\t= " + parameterValue;
	}

	public void apply(OperatorProcess process, Map<String, String> nameTranslation) {
		String opName = null;
		if (nameTranslation != null) {
			opName = nameTranslation.get(this.operator);
		}
		if (opName == null) {
			opName = this.operator;
		}
//		process.getLogger().fine(
//				"Setting parameter '" + parameterKey + "' of operator '" + opName + "' to '" + parameterValue + "'.");
		Operator operator = process.getOperator(opName);
		if (operator == null) {
			process.getLogger().warn("No such operator: '" + opName + "'.");
		} else {
			operator.getParameters().setParameter(parameterKey, parameterValue);
		}
	}
}
