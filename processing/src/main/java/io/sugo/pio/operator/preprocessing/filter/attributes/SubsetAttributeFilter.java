package io.sugo.pio.operator.preprocessing.filter.attributes;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeAttributes;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;

import java.util.LinkedList;
import java.util.List;

import static io.sugo.pio.parameter.ParameterTypeAttributes.ATTRIBUTE_SEPARATOR_CHARACTER;

/**
 * A filter condition for subsets of attributes.
 * 
 */
public class SubsetAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_ATTRIBUTES = "attributes";

//	public static final String PARAMETER_ATTRIBUTES_SEPERATOR = "\\|";

	private String attributeNames;

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {
		attributeNames = operator.getParameterAsString(PARAMETER_ATTRIBUTES);
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		if ((attributeNames == null) || (attributeNames.length() == 0)) {
			return MetaDataInfo.YES;
		}
		boolean found = false;
		for (String attributeName : attributeNames.split(ATTRIBUTE_SEPARATOR_CHARACTER)) {
			if (attribute.getName().equals(attributeName)) {
				found = true;
			}
		}
		return found ? MetaDataInfo.NO : MetaDataInfo.YES;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		if (attributeNames == null || attributeNames.length() == 0) {
			return ScanResult.REMOVE;
		}
		for (String attributeName : attributeNames.split(ATTRIBUTE_SEPARATOR_CHARACTER)) {
			if (attribute.getName().equals(attributeName)) {
				return ScanResult.KEEP;
			}
		}
		return ScanResult.REMOVE;
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, final InputPort inPort, int... valueTypes) {
		List<ParameterType> types = new LinkedList<ParameterType>();
		ParameterType type = new ParameterTypeAttributes(PARAMETER_ATTRIBUTES, I18N.getMessage("pio.AttributeFilter.subset.attributes"),
				inPort, false, valueTypes);
		types.add(type);
		return types;
	}
}
