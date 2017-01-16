package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;

/**
 * This filter actually does nothing and removes no attribute. It is needed to create a behavior
 * consistent with earlier versions and to allow to bundle the filtering with an
 * AttributeSubsetPreprocessing operator.
 * 
 * @author Sebastian Land
 */
public class TransparentAttributeFilter extends AbstractAttributeFilterCondition {

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		return MetaDataInfo.NO;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		return ScanResult.KEEP;
	}

}
