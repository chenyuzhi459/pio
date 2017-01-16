package io.sugo.pio.operator.processing.filter;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.tools.Ontology;


public class RegexpAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_REGULAR_EXPRESSION = "regular_expression";

	private String attributeNameRegexp;
	private String exceptRegexp = null;

	@Override
	public void init(ParameterHandler operator) throws UserError {
		attributeNameRegexp = operator.getParameterAsString(PARAMETER_REGULAR_EXPRESSION);
		if ((attributeNameRegexp == null) || (attributeNameRegexp.length() == 0)) {
			throw new UserError((operator instanceof Operator) ? (Operator) operator : null, 904,
					"The condition for attribute names needs a parameter string.");
		}
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		if (attribute.getName().matches(attributeNameRegexp)) {
			if (exceptRegexp != null) {
				if (attribute.getName().matches(exceptRegexp)) {
					return ScanResult.REMOVE;
				}
			}
			return ScanResult.KEEP;
		} else {
			return ScanResult.REMOVE;
		}
	}

	private boolean isOfAllowedType(int valueType, int[] allowedValueTypes) {
		boolean isAllowed = false;
		for (int type : allowedValueTypes) {
			isAllowed |= Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, type);
		}
		return isAllowed;
	}
}
