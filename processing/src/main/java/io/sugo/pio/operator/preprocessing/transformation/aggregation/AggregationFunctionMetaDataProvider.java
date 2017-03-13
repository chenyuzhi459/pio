package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;


/**
 * This is the interface for all classes, that implement the delivery of meta data for certain
 * {@link AggregationFunction}s.
 * 
 */
public interface AggregationFunctionMetaDataProvider {

	/**
	 * This method must return an {@link AttributeMetaData} for the target attribute, or null if the
	 * source attribute's valuetype is incompatible. Then an error should be registered on the
	 * inputport unless the port is null. In cases, where the sourceAttribute is unknown during the
	 * aggregation, only a warning should be registered in the port, but it should be assumed, that
	 * the attribute will be present.
	 */
	public AttributeMetaData getTargetAttributeMetaData(AttributeMetaData sourceAttribute, InputPort port);
}
