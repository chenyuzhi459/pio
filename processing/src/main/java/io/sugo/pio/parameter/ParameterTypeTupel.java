package io.sugo.pio.parameter;

import io.sugo.pio.tools.Pair;
import io.sugo.pio.tools.Tools;
import org.w3c.dom.Element;

import java.util.List;


/**
 * This is a parameter type that contains a number of subtypes. But unlike the
 * {@link ParameterTypeList} or {@link ParameterTypeEnumeration}, these subtypes are treated as one
 * value. In the gui these subtypes are shown beside each other.
 *
 * @author Sebastian Land
 */
public class ParameterTypeTupel extends CombinedParameterType {

	private static final long serialVersionUID = 7292052301201204321L;

	private static final String ELEMENT_CHILD_TYPE = "ChildType";
	private static final String ELEMENT_CHILD_TYPES = "ChildTypes";
	private static final String ELEMENT_DEFAULT_ENTRIES = "DefaultEntries";
	private static final String ELEMENT_DEFAULT_ENTRY = "Entry";
	private static final String ATTRIBUTE_IS_NULL = "is-null";

	// // only one character allowed
	// private static final String ESCAPE_CHAR = "\\";
	// private static final String ESCAPE_CHAR_REGEX = "\\\\";
	// // only one character allowed
	// private static final String SEPERATOR_CHAR_REGEX = "\\.";
	private static final char ESCAPE_CHAR = '\\';
	private static final char XML_SEPERATOR_CHAR = '.';
	private static final char[] XML_SPECIAL_CHARACTERS = new char[] { XML_SEPERATOR_CHAR };
	private static final char INTERNAL_SEPERATOR_CHAR = '.'; // Parameters.PAIR_SEPARATOR; //'.';
	private static final char[] INTERNAL_SPECIAL_CHARACTERS = new char[] { INTERNAL_SEPERATOR_CHAR };

	private Object[] defaultValues = null;

	private ParameterType[] types;

	public ParameterTypeTupel(String key, String description, ParameterType... parameterTypes) {
		super(key, description, parameterTypes);
		this.types = parameterTypes;
	}

	@Override
	public Object getDefaultValue() {
		if (defaultValues == null) {
			String[] defaultValues = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				defaultValues[i] = types[i].getDefaultValue() == null ? "" : types[i].getDefaultValue() + "";
			}
			return ParameterTypeTupel.transformTupel2String(defaultValues);
		} else {
			String[] defStrings = new String[defaultValues.length];
			for (int i = 0; i < defaultValues.length; i++) {
				defStrings[i] = defaultValues[i] + "";
			}
			return ParameterTypeTupel.transformTupel2String(defStrings);
		}
	}

	@Override
	public String getRange() {
		return "tupel";
	}

	@Override
	public boolean isNumerical() {
		return false;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValues = (Object[]) defaultValue;
	}

	public ParameterType getFirstParameterType() {
		return types[0];
	}

	public ParameterType getSecondParameterType() {
		return types[1];
	}

	public ParameterType[] getParameterTypes() {
		return types;
	}

	public static String[] transformString2Tupel(String parameterValue) {
		if (parameterValue == null || parameterValue.isEmpty()) {
			return new String[2];
		}
		List<String> split = Tools.unescape(parameterValue, ESCAPE_CHAR, INTERNAL_SPECIAL_CHARACTERS,
				INTERNAL_SEPERATOR_CHAR);
		while (split.size() < 2) {
			split.add(null);
		}
		return split.toArray(new String[split.size()]);
	}

	public static String transformTupel2String(String firstValue, String secondValue) {
		return Tools.escape(firstValue, ESCAPE_CHAR, INTERNAL_SPECIAL_CHARACTERS) + INTERNAL_SEPERATOR_CHAR
				+ Tools.escape(secondValue, ESCAPE_CHAR, INTERNAL_SPECIAL_CHARACTERS);
	}

	public static String transformTupel2String(Pair<String, String> pair) {
		return transformTupel2String(pair.getFirst(), pair.getSecond());
	}

	public static String transformTupel2String(String[] tupel) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tupel.length; i++) {
			if (i > 0) {
				builder.append(INTERNAL_SEPERATOR_CHAR);
			}
			if (tupel[i] != null) {
				builder.append(Tools.escape(tupel[i], ESCAPE_CHAR, INTERNAL_SPECIAL_CHARACTERS));
			}
		}
		return builder.toString();
	}
}
