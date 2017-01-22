package io.sugo.pio.parameter;


import io.sugo.pio.tools.Tools;

import java.util.List;

/**
 * This is a parameter type that will present a list of values. This would have been made a better
 * list than {@link ParameterTypeList} itself, which is more a mapping list with a pair of values.
 * This one only has one inner type.
 *
 * @author Sebastian Land
 */
public class ParameterTypeEnumeration extends CombinedParameterType {

	private static final long serialVersionUID = -3677952200700007724L;

	private static final String ELEMENT_CHILD_TYPE = "ChildType";
	private static final String ELEMENT_DEFAULT_VALUE = "Default";

	private static final char ESCAPE_CHAR = '\\';
	private static final char SEPERATOR_CHAR = ','; // Parameters.RECORD_SEPARATOR; //
	private static final char[] SPECIAL_CHARACTERS = new char[] { SEPERATOR_CHAR };

	private Object defaultValue;

	private ParameterType type;

	public ParameterTypeEnumeration(String key, String description, ParameterType parameterType) {
		super(key, description, parameterType);
		this.type = parameterType;
	}

	public ParameterTypeEnumeration(String key, String description, ParameterType parameterType, boolean expert) {
		this(key, description, parameterType);
//		setExpert(expert);
	}

	@Override
	public boolean isNumerical() {
		return false;
	}

	@Override
	public String getRange() {
		return "enumeration";
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public ParameterType getValueType() {
		return type;
	}

	public static String transformEnumeration2String(List<String> list) {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (String string : list) {
			if (!isFirst) {
				builder.append(SEPERATOR_CHAR);
			}
			if (string != null) {
				builder.append(Tools.escape(string, ESCAPE_CHAR, SPECIAL_CHARACTERS));
			}
			isFirst = false;
		}
		return builder.toString();
	}

	public static String[] transformString2Enumeration(String parameterValue) {
		if (parameterValue == null || "".equals(parameterValue)) {
			return new String[0];
		}
		List<String> split = Tools.unescape(parameterValue, ESCAPE_CHAR, SPECIAL_CHARACTERS, SEPERATOR_CHAR);
		return split.toArray(new String[split.size()]);
	}

}
