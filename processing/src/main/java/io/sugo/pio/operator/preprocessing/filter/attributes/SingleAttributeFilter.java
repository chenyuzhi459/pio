package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeAttribute;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;

import java.util.LinkedList;
import java.util.List;


/**
 * @author Tobias Malbrecht
 */
public class SingleAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_ATTRIBUTE = "attribute";

	private String attributeName;

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {
		attributeName = operator.getParameterAsString(PARAMETER_ATTRIBUTE);
		if (attributeName == null || attributeName.length() == 0) {
			throw new UserError(operator instanceof Operator ? (Operator) operator : null,
					"pio.error.no_attribute_selected");
		}
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		if (attributeName == null || attributeName.length() == 0) {
			throw new ConditionCreationException(
					"The condition for a single attribute needs a non-empty attribute parameter string.");
		}
		return attribute.getName().equals(attributeName) ? MetaDataInfo.NO : MetaDataInfo.YES;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		if (attribute.getName().equals(attributeName)) {
			return ScanResult.KEEP;
		} else {
			return ScanResult.REMOVE;
		}
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, InputPort inPort, int... valueTypes) {
		List<ParameterType> types = new LinkedList<>();
		ParameterType type = new ParameterTypeAttribute(PARAMETER_ATTRIBUTE,
				I18N.getMessage("pio.SingleAttributeFilter.attribute"),
				inPort, true, valueTypes);
		types.add(type);
		return types;
	}
}
