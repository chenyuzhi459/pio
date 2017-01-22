package io.sugo.pio.tools.expression.internal.function.eval;


import io.sugo.pio.tools.expression.Constant;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.internal.SimpleConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Type constants that can be used as a second argument in the eval function.
 *
 * @author Gisa Schaefer
 *
 */
public enum TypeConstants {

	INSTANCE;

	private static final String USED_IN_EVAL = "used in eval";
	private final List<Constant> typeConstants = new ArrayList<>(5);
	private final Map<String, ExpressionType> conversionMap = new HashMap<>(5);

	private TypeConstants() {
		conversionMap.put("NOMINAL", ExpressionType.STRING);
		conversionMap.put("BINOMINAL", ExpressionType.BOOLEAN);
		conversionMap.put("DATE", ExpressionType.DATE);
		conversionMap.put("REAL", ExpressionType.DOUBLE);
		conversionMap.put("INTEGER", ExpressionType.INTEGER);

		for (String constantName : conversionMap.keySet()) {
			typeConstants.add(new SimpleConstant(constantName, constantName, USED_IN_EVAL));
		}

	}

	/**
	 * Returns the key associated to the constants contained. The constants are shown in the
	 * {@link ExpressionPropertyDialog} under the category defined by
	 * "gui.dialog.function_input.key.constant_category".
	 *
	 * @return the key for the constant category
	 */
	public String getKey() {
		return "core.type_constants";
	}

	/**
	 * @return all type constants
	 */
	public List<Constant> getConstants() {
		return typeConstants;
	}

	/**
	 * Returns the {@link ExpressionType} that is associated with the constant with the given name.
	 *
	 * @param name
	 *            the name of the constant
	 * @return the associated expression type
	 */
	public ExpressionType getTypeForName(String name) {
		return conversionMap.get(name);
	}

	/**
	 * Returns the name of the constant that is associated with the type.
	 *
	 * @param type
	 *            the expression type
	 * @return the constant name
	 */
	public String getNameForType(ExpressionType type) {
		for (Map.Entry<String, ExpressionType> entry : conversionMap.entrySet()) {
			String key = entry.getKey();
			ExpressionType value = entry.getValue();
			if (value == type) {
				return key;
			}
		}
		// cannot happen since all enum entries are present
		return null;
	}

	/**
	 * @return a string containing the names of all type constants
	 */
	public String getValidConstantsString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String name : conversionMap.keySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append("'");
			builder.append(name);
			builder.append("'");
		}
		return builder.toString();
	}

}
