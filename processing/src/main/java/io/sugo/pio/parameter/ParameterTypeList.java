package io.sugo.pio.parameter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * A parameter type for parameter lists. Operators ask for the list of the specified values with
 * . Please note that in principle
 * arbitrary parameter types can be used for the list values. Internally, however, all values are
 * transformed to strings. Therefore, operators retrieving values from non-string lists (for example
 * for a parameter type category) have to transform the values themself, e.g. with the following
 * code:<br/>
 * <br/>
 *
 * <code>int index = ((ParameterTypeCategory)((ParameterTypeList)getParameters().getParameterType(PARAMETER_LIST)).getValueType()).getIndex(pair[1]);</code>
 *
 * @author Ingo Mierswa, Simon Fischer
 */
public class ParameterTypeList extends CombinedParameterType {

	private static final long serialVersionUID = -6101604413822993455L;

	private static final String ELEMENT_KEY_TYPE = "KeyType";

	private static final String ELEMENT_VALUE_TYPE = "ValueType";
	private static final String ELEMENT_DEFAULT_ENTRIES = "DefaultEntries";
	private static final String ELEMENT_ENTRY = "Entry";
	private static final String ATTRIBUTE_KEY = "key";
	private static final String ATTRIBUTE_VALUE = "value";

	private List<String[]> defaultList = new LinkedList<>();

	private final ParameterType valueType;
	private final ParameterType keyType;

	/**
	 * This constructor is deprecated, because it does not provide enough information for user guidance
	 */
	public ParameterTypeList(String key, String description, ParameterType valueType) {
		this(key, description, valueType, new LinkedList<String[]>());
	}

	/**
	 * This constructor is deprecated, because it does not provide enough information for user guidance
	 */
	public ParameterTypeList(String key, String description, ParameterType valueType, List<String[]> defaultList) {
		super(key, description);
		this.defaultList = defaultList;
		this.valueType = valueType;
		this.keyType = new ParameterTypeString(key, description, "");
		if (valueType.getDescription() == null) {
			valueType.setDescription(description);
		}
	}


	public ParameterTypeList(String key, String description, ParameterType keyType, ParameterType valueType) {
		this(key, description, keyType, valueType, new LinkedList<String[]>());
	}

	public ParameterTypeList(String key, String description, ParameterType keyType, ParameterType valueType,
							 List<String[]> defaultList) {
		super(key, description, keyType, valueType);
		this.defaultList = defaultList;
		this.valueType = valueType;
		this.keyType = keyType;
	}

	public ParameterType getValueType() {
		return valueType;
	}

	public ParameterType getKeyType() {
		return keyType;
	}

	@Override
	public List<String[]> getDefaultValue() {
		return defaultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	// TODO: Introduce Typing??
	public void setDefaultValue(Object defaultValue) {
		this.defaultList = (List<String[]>) defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object value) {
		if (value instanceof String) {
			return transformList2String(transformString2List(value.toString()));
		} else {
			return transformList2String((List<String[]>) value);
		}
	}

	public static String transformList2String(List<String[]> parameterList) {
		StringBuffer result = new StringBuffer();
		Iterator<String[]> i = parameterList.iterator();
		boolean first = true;
		while (i.hasNext()) {
			String[] objects = i.next();
			if (objects.length != 2) {
				continue;
			}

			String firstToken = objects[0];
			String secondToken = objects[1];
			if (!first) {
				result.append(Parameters.RECORD_SEPARATOR);
			}
			if (secondToken != null) {
				if (firstToken != null) {
					result.append(firstToken);
				}
				result.append(Parameters.PAIR_SEPARATOR);
				if (secondToken != null) {
					result.append(secondToken);
				}
			}
			first = false;
		}
		return result.toString();
	}

	public static List<String[]> transformString2List(String listString) {
		List<String[]> result = new LinkedList<>();
		String[] splittedList = listString.split(Character.valueOf(Parameters.RECORD_SEPARATOR).toString());
		for (String record : splittedList) {
			if (record.length() > 0) {
				String[] pair = record.split(Character.valueOf(Parameters.PAIR_SEPARATOR).toString());
				if (pair.length == 2 && pair[0].length() > 0 && pair[1].length() > 0) {
					result.add(new String[] { pair[0], pair[1] });
				}
			}
		}
		return result;
	}

	@Override
	public String getRange() {
		return "list";
	}

	@Override
	public boolean isNumerical() {
		return false;
	}
}
