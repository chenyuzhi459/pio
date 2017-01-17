package io.sugo.pio.ports.metadata;

import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * @author Simon Fischer
 */
public class ExampleSetPassThroughRule extends PassThroughRule {

	SetRelation relation;

	public ExampleSetPassThroughRule(InputPort inputPort, OutputPort outputPort, SetRelation attributeSetRelation) {
		super(inputPort, outputPort, false);
		this.relation = attributeSetRelation;
	}

	@Override
	public MetaData modifyMetaData(MetaData metaData) {
		if (metaData instanceof ExampleSetMetaData) {
			ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
			if (relation != null) {
				emd.mergeSetRelation(relation);
			}
			try {
				return modifyExampleSet(emd);
			} catch (UndefinedParameterError e) {
				return emd;
			}

		} else {
			return metaData;
		}
	}

	/**
	 * This method might be used for convenience for slight modifications of the exampleSet like
	 * adding an attribute. Subclasses might override this method.
	 * 
	 * @throws UndefinedParameterError
	 */
	public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
		return metaData;
	}

}
