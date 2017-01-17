package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;

/**
 * This class implements a no missing value filter for attributes. Attributes are filtered and hence
 * be removed from exampleSet if there are missing values in one of the examples in this attribute.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class NoMissingValuesAttributeFilter extends AbstractAttributeFilterCondition {

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler) {
		switch (attribute.containsMissingValues()) {
			case YES:
				return MetaDataInfo.YES;
			case NO:
				return MetaDataInfo.NO;
			default:
				return MetaDataInfo.UNKNOWN;
		}
	}

	@Override
	public boolean isNeedingScan() {
		return true;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) {
		return ScanResult.UNCHECKED;
	}

	@Override
	public ScanResult check(Attribute attribute, Example example) {
		if (Double.isNaN(example.getValue(attribute))) {
			return ScanResult.REMOVE;
		} else {
			return ScanResult.UNCHECKED;
		}
	}
}
