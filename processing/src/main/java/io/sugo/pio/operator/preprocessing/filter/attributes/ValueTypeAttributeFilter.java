package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;
import io.sugo.pio.tools.Ontology;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Tobias Malbrecht
 */
public class ValueTypeAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_VALUE_TYPE = "value_type";
	public static final String PARAMETER_ADD_EXCEPTION = "use_value_type_exception";
	public static final String PARAMETER_EXCEPT_VALUE_TYPE = "except_value_type";

	private int valueType;
	private int exceptValueType;

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {
		valueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(operator.getParameterAsString(PARAMETER_VALUE_TYPE));
		if (valueType < 0 || valueType >= Ontology.VALUE_TYPE_NAMES.length) {
			throw new ConditionCreationException("Unknown value type selected.");
		}
		String exceptValueTypeName = operator.getParameterAsString(PARAMETER_EXCEPT_VALUE_TYPE);
		if (operator.getParameterAsBoolean(PARAMETER_ADD_EXCEPTION)) {
			exceptValueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(exceptValueTypeName);
			if (valueType < 0 || valueType >= Ontology.VALUE_TYPE_NAMES.length) {
				throw new ConditionCreationException("Unknown value type selected.");
			}
		} else {
			exceptValueType = -1;
		}
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), valueType)) {
			if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), exceptValueType)) {
				return MetaDataInfo.YES;
			} else {
				return MetaDataInfo.NO;
			}
		}
		return MetaDataInfo.YES;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), valueType)) {
			if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), exceptValueType)) {
				return ScanResult.REMOVE;
			} else {
				return ScanResult.KEEP;
			}
		}
		return ScanResult.REMOVE;
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, Port inPort, int... valueTypes) {
		List<ParameterType> types = new LinkedList<ParameterType>();
		Set<String> valueTypeSet = new LinkedHashSet<String>();
		for (String valueTypeName : Ontology.ATTRIBUTE_VALUE_TYPE.getNames()) {
			int valueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(valueTypeName);
			for (int parent : valueTypes) {
				if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, parent)) {
					valueTypeSet.add(Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(valueType));
				}
			}
		}
		String[] valueTypeNames = new String[valueTypeSet.size()];
		valueTypeNames = valueTypeSet.toArray(valueTypeNames);

		String[] exceptValueTypeNames = new String[valueTypeSet.size()];
		exceptValueTypeNames = valueTypeSet.toArray(valueTypeNames);

		types.add(new ParameterTypeCategory(PARAMETER_VALUE_TYPE, I18N.getMessage("pio.ValueTypeAttributeFilter.value_type"),
				valueTypeNames, 0));
		types.add(new ParameterTypeBoolean(
				PARAMETER_ADD_EXCEPTION,
				I18N.getMessage("pio.ValueTypeAttributeFilter.use_value_type_exception"),
				false));
		ParameterType type = new ParameterTypeCategory(PARAMETER_EXCEPT_VALUE_TYPE,
				I18N.getMessage("pio.ValueTypeAttributeFilter.except_value_type"),
				exceptValueTypeNames, exceptValueTypeNames.length - 1);
		type.registerDependencyCondition(new BooleanParameterCondition(operator, PARAMETER_ADD_EXCEPTION, true, true));
		types.add(type);

		return types;
	}
}
