package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;
import java.util.LinkedList;


/**
 * Passes meta data from an input port to an output port or generates a new one if the input meta
 * data is null.
 * 
 */
public class PassThroughOrGenerateRule implements MDTransformationRule {

	private InputPort inputPort;
	private OutputPort outputPort;
	private MetaData generatedMetaData;
	private Collection<PassThroughOrGenerateRuleCondition> passThroughConditions = new LinkedList<PassThroughOrGenerateRuleCondition>();
	private Collection<PassThroughOrGenerateRuleCondition> generateConditions = new LinkedList<PassThroughOrGenerateRuleCondition>();

	public PassThroughOrGenerateRule(InputPort inputPort, OutputPort outputPort, MetaData generatedMetaData) {
		this.inputPort = inputPort;
		this.outputPort = outputPort;
		this.generatedMetaData = generatedMetaData;
	}

	@Override
	public void transformMD() {
		MetaData inputMD = inputPort.getMetaData();
		if (inputMD != null) {
			boolean ok = true;
			for (PassThroughOrGenerateRuleCondition condition : passThroughConditions) {
				if (!condition.conditionFullfilled()) {
					condition.registerErrors();
					ok = false;
				}
			}
			if (ok) {
				outputPort.deliverMD(transformPassedThrough(inputMD.clone()));
			}
		} else {
			boolean ok = true;
			for (PassThroughOrGenerateRuleCondition condition : generateConditions) {
				if (!condition.conditionFullfilled()) {
					condition.registerErrors();
					ok = false;
				}
			}
			if (ok) {
				outputPort.deliverMD(transformGenerated(generatedMetaData.clone()));
			}
		}
	}

	/**
	 * Can be overridden to make additional transformations to the generated meta data.
	 */
	public MetaData transformGenerated(MetaData md) {
		md.addToHistory(outputPort);
		return md;
	}

	/**
	 * Can be overridden to make additional transformations to the meta data passed through from the
	 * input port.
	 */
	public MetaData transformPassedThrough(MetaData md) {
		md.addToHistory(outputPort);
		return md;
	}

	/**
	 * this method allows to add additional conditions for passing through
	 * 
	 * @param condition
	 */
	public void addPassThroughCondition(PassThroughOrGenerateRuleCondition condition) {

		passThroughConditions.add(condition);
	}

	/**
	 * This allows to add additional conditions for generation
	 * 
	 * @param condition
	 */
	public void addGenerateCondition(PassThroughOrGenerateRuleCondition condition) {
		generateConditions.add(condition);
	}
}
