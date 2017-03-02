package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeRegexp;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;
import io.sugo.pio.tools.Ontology;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * This Attribute Filter removes every attribute, which name does not match the Regular expression
 * given by parameter. A data scan is not needed.
 * 
 * @author Sebastian Land, Ingo Mierswa, Tobias Malbrecht
 */
public class RegexpAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_REGULAR_EXPRESSION = "regular_expression";
	public static final String PARAMETER_ADD_EXCEPTION = "use_except_expression";
	public static final String PARAMETER_EXCEPT_REGULAR_EXPRESSION = "except_regular_expression";

	private String attributeNameRegexp;
	private String exceptRegexp = null;

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {
		attributeNameRegexp = operator.getParameterAsString(PARAMETER_REGULAR_EXPRESSION);
		if ((attributeNameRegexp == null) || (attributeNameRegexp.length() == 0)) {
			throw new UserError((operator instanceof Operator) ? (Operator) operator : null,
					"pio.error.cannot_instantiate",
					"The condition for attribute names needs a parameter string.");
		}
		if (operator.isParameterSet(PARAMETER_EXCEPT_REGULAR_EXPRESSION)
				&& operator.getParameterAsBoolean(PARAMETER_ADD_EXCEPTION)) {
			exceptRegexp = operator.getParameterAsString(PARAMETER_EXCEPT_REGULAR_EXPRESSION);
		}
		if ((exceptRegexp != null) && (exceptRegexp.length() == 0)) {
			exceptRegexp = null;
		}
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		try {
			if (attribute.getName().matches(attributeNameRegexp)) {
				if (exceptRegexp != null) {
					if (attribute.getName().matches(exceptRegexp)) {
						return MetaDataInfo.YES;
					} else {
						return MetaDataInfo.NO;
					}
				}
				return MetaDataInfo.NO;
			} else {
				return MetaDataInfo.YES;
			}
		} catch (PatternSyntaxException e) {
			return MetaDataInfo.UNKNOWN;
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

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, final InputPort inPort, final int... valueTypes) {
		List<ParameterType> types = new LinkedList<ParameterType>();
		types.add(new ParameterTypeRegexp(PARAMETER_REGULAR_EXPRESSION,
				"A regular expression for the names of the attributes which should be kept.", true) {

			private static final long serialVersionUID = 8133149560984042644L;

			@Override
			public Collection<String> getPreviewList() {
				Collection<String> regExpPreviewList = new LinkedList<String>();
				if (inPort == null) {
					return null;
				}
				MetaData metaData = inPort.getMetaData();
				if (metaData instanceof ExampleSetMetaData) {
					ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
					for (AttributeMetaData amd : emd.getAllAttributes()) {
						if (isOfAllowedType(amd.getValueType(), valueTypes)) {
							regExpPreviewList.add(amd.getName());
						}
					}
				}
				return regExpPreviewList;
			}
		});
		types.add(new ParameterTypeBoolean(
				PARAMETER_ADD_EXCEPTION,
				"If enabled, an exception to the specified regular expression might be specified. Attributes of matching this will be filtered out, although matching the first expression.",
				false));

		ParameterType type = (new ParameterTypeRegexp(
				PARAMETER_EXCEPT_REGULAR_EXPRESSION,
				"A regular expression for the names of the attributes which should be filtered out although matching the above regular expression.",
				true) {

			private static final long serialVersionUID = 81331495609840426L;

			@Override
			public Collection<String> getPreviewList() {
				Collection<String> regExpPreviewList = new LinkedList<String>();
				if (inPort == null) {
					return null;
				}
				MetaData metaData = inPort.getMetaData();
				if (metaData instanceof ExampleSetMetaData) {
					ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
					for (AttributeMetaData amd : emd.getAllAttributes()) {
						if (isOfAllowedType(amd.getValueType(), valueTypes)) {
							regExpPreviewList.add(amd.getName());
						}
					}
				}
				return regExpPreviewList;
			}
		});
		type.registerDependencyCondition(new BooleanParameterCondition(operator, PARAMETER_ADD_EXCEPTION, true, true));
		types.add(type);

		return types;
	}
}
