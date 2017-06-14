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
 * This condition filters out all attributes whose block type does not match the one given in the
 * parameter block_type. This can be useful e.g. for preprocessing operators that can handle only
 * series attributes. Features of the type mentioned in except_block_type are removed even if they
 * match block_type.
 * 
 * @author Sebastian Land
 */
public class BlockTypeAttributeFilter extends AbstractAttributeFilterCondition {

	public static final String PARAMETER_BLOCK_TYPE = "block_type";
	public static final String PARAMETER_ADD_EXCEPTION = "use_block_type_exception";
	public static final String PARAMETER_EXCEPT_BLOCK_TYPE = "except_block_type";

	private int blockType;
	private int exceptBlockType;

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {
		blockType = Ontology.ATTRIBUTE_BLOCK_TYPE.mapName(operator.getParameterAsString(PARAMETER_BLOCK_TYPE));
		if (blockType < 0 || blockType >= Ontology.BLOCK_TYPE_NAMES.length) {
			throw new ConditionCreationException("Unknown value type selected.");
		}
		String exceptValueTypeName = operator.getParameterAsString(PARAMETER_EXCEPT_BLOCK_TYPE);
		if (operator.getParameterAsBoolean(PARAMETER_ADD_EXCEPTION)) {
			exceptBlockType = Ontology.ATTRIBUTE_BLOCK_TYPE.mapName(exceptValueTypeName);
			if (blockType < 0 || blockType >= Ontology.BLOCK_TYPE_NAMES.length) {
				throw new ConditionCreationException("Unknown value type selected.");
			}
		} else {
			exceptBlockType = -1;
		}
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler)
			throws ConditionCreationException {
		return MetaDataInfo.UNKNOWN;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		if (Ontology.ATTRIBUTE_BLOCK_TYPE.isA(attribute.getValueType(), blockType)) {
			if (exceptBlockType > 0 && Ontology.ATTRIBUTE_BLOCK_TYPE.isA(attribute.getValueType(), exceptBlockType)) {
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
		for (String valueTypeName : Ontology.ATTRIBUTE_BLOCK_TYPE.getNames()) {
			int valueType = Ontology.ATTRIBUTE_BLOCK_TYPE.mapName(valueTypeName);
			for (int parent : valueTypes) {
				if (Ontology.ATTRIBUTE_BLOCK_TYPE.isA(valueType, parent)) {
					valueTypeSet.add(Ontology.ATTRIBUTE_BLOCK_TYPE.mapIndex(valueType));
				}
			}
		}
		String[] valueTypeNames = new String[valueTypeSet.size()];
		valueTypeNames = valueTypeSet.toArray(valueTypeNames);

		String[] exceptValueTypeNames = new String[valueTypeSet.size()];
		exceptValueTypeNames = valueTypeSet.toArray(valueTypeNames);

		types.add(new ParameterTypeCategory(PARAMETER_BLOCK_TYPE, I18N.getMessage("pio.BlockTypeAttributeFilter.block_type"), valueTypeNames, 0));
		types.add(new ParameterTypeBoolean(PARAMETER_ADD_EXCEPTION,
				I18N.getMessage("pio.BlockTypeAttributeFilter.use_block_type_exception"), false));
		ParameterType type = new ParameterTypeCategory(PARAMETER_EXCEPT_BLOCK_TYPE, I18N.getMessage("pio.BlockTypeAttributeFilter.except_block_type"),
				exceptValueTypeNames, exceptValueTypeNames.length - 1);
		type.registerDependencyCondition(new BooleanParameterCondition(operator, PARAMETER_ADD_EXCEPTION, true, true));
		types.add(type);
		return types;
	}
}
