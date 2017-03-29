package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;

import java.util.LinkedList;
import java.util.List;


/**
 * This is an abstract class for all normalization methods. It returns just an empty list of
 * {@link ParameterType}s and does not perform any init code.
 * 
 */
public abstract class AbstractNormalizationMethod implements NormalizationMethod {

	@Override
	public void init() {}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler handler) {
		return new LinkedList<ParameterType>();
	}

}
