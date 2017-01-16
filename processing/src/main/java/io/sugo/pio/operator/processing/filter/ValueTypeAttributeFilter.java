package io.sugo.pio.operator.processing.filter;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.MetaDataInfo;
import io.sugo.pio.tools.Ontology;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class ValueTypeAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_VALUE_TYPE = "value_type";
	public static final String PARAMETER_ADD_EXCEPTION = "use_value_type_exception";
	public static final String PARAMETER_EXCEPT_VALUE_TYPE = "except_value_type";

	private int valueType;
	private int exceptValueType;

	@Override
	public void init(ParameterHandler operator) throws UserError {
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
	public List<ParameterType> getParameterTypes(ParameterHandler operator, InputPort inPort, int... valueTypes) {
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

		types.add(new ParameterTypeCategory(PARAMETER_VALUE_TYPE, "The value type of the attributes.", valueTypeNames, 0
				));
		types.add(new ParameterTypeBoolean(
				PARAMETER_ADD_EXCEPTION,
				"If enabled, an exception to the specified value type might be specified. Attributes of this type will be filtered out, although matching the first specified type.",
				false));
		ParameterType type = new ParameterTypeCategory(PARAMETER_EXCEPT_VALUE_TYPE, "Except this value type.",
				exceptValueTypeNames, exceptValueTypeNames.length - 1);
		type.registerDependencyCondition(new BooleanParameterCondition(operator, PARAMETER_ADD_EXCEPTION, true, true));
		types.add(type);

		return types;
	}
}
