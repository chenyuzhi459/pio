package io.sugo.pio.operator.learner;


import io.sugo.pio.operator.OperatorCapability;

/**
 * @author Sebastian Land
 * 
 */
public interface CapabilityProvider {

	/**
	 * The property name for &quot;Indicates if only a warning should be made if learning
	 * capabilities are not fulfilled (instead of breaking the process).&quot;
	 */
	public static final String PROPERTY_RAPIDMINER_GENERAL_CAPABILITIES_WARN = "rapidminer.general.capabilities.warn";

	/**
	 * Checks for Learner capabilities. Should return true if the given capability is supported.
	 */
	public boolean supportsCapability(OperatorCapability capability);

}
