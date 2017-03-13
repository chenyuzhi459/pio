package io.sugo.pio.tools.math;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.UndefinedParameterError;


/**
 * Specifies how roc plots are evaluate: - first count correct classifications, then count incorrect
 * ones - first count incorrect classifications, then count correct ones - distribute them evenly.
 * 
 */
public enum ROCBias {

	PESSIMISTIC,

	NEUTRAL,

	OPTIMISTIC;

	/** Parameter to select the bias type. */
	public static final String PARAMETER_NAME_ROC_BIAS = "roc_bias";

	public static ROCBias getROCBiasParameter(Operator operator) throws UndefinedParameterError {
		return ROCBias.values()[operator.getParameterAsInt(PARAMETER_NAME_ROC_BIAS)];
	}

	public static ParameterType makeParameterType() {
		String[] values = new String[ROCBias.values().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = ROCBias.values()[i].toString().toLowerCase();
		}
		return new ParameterTypeCategory(PARAMETER_NAME_ROC_BIAS,
				"Determines how the ROC (and AUC) are evaluated: Count correct predictions first, last, or alternatingly",
				values, OPTIMISTIC.ordinal());
	}
}
