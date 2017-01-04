package io.sugo.pio.parameter;

import io.sugo.pio.tools.Parameter;

import java.util.Map;


/**
 * 
 * @author Sebastian Land
 * 
 */
public interface ParameterWriter {

	/**
	 * This method will be called whenever the parameters will be saved.
	 */
	public void writeParameters(Map<String, Parameter> parameters);
}
