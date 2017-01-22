package io.sugo.pio.tools.expression.internal;


import io.sugo.pio.tools.expression.Constant;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInput;
import io.sugo.pio.tools.expression.Resolver;

import java.util.*;


/**
 * {@link Resolver} for the {@link Constant}s supplied in the constructor. These constants will be
 * shown in the expression parser dialog ({@link ExpressionPropertyDialog}) under the category
 * defined by "gui.dialog.function_input.key.constant_category".
 *
 * @author Gisa Schaefer
 */
public class ConstantResolver implements Resolver {

    /**
     * the key suffix to get the category name
     */
    private static final String KEY_SUFFIX = ".constant_category";

    private final Map<String, Constant> constantMap;
    private final String categoryName;

    private static final String GUI_KEY_PREFIX = "gui.dialog.function_input.";

    /**
     * Creates a {@link Resolver} that knows the constants. The constants are used by the
     * {@link ExpressionParser} and shown in the {@link ExpressionPropertyDialog} under the category
     * defined by "gui.dialog.function_input.key.constant_category".
     *
     * @param key       the key for the category name
     * @param constants the constants this resolver knows
     */
    public ConstantResolver(String key, List<Constant> constants) {
        constantMap = new LinkedHashMap<>();
        for (Constant constant : constants) {
            if (constant != null) {
                constantMap.put(constant.getName(), constant);
            }
        }
        categoryName = GUI_KEY_PREFIX + key + KEY_SUFFIX;//I18N.getGUIMessage(GUI_KEY_PREFIX + key + KEY_SUFFIX);
    }

    @Override
    public List<FunctionInput> getAllVariables() {
        List<FunctionInput> functionInputs = new ArrayList<>(constantMap.size());
        for (Constant constant : constantMap.values()) {
            functionInputs.add(new FunctionInput(FunctionInput.Category.CONSTANT, categoryName, constant.getName(), constant.getType()
                    .getAttributeType(), constant.getAnnotation(), false, constant.isInvisible()));
        }
        return functionInputs;
    }

    @Override
    public ExpressionType getVariableType(String variableName) {
        if (constantMap.get(variableName) == null) {
            return null;
        }
        return constantMap.get(variableName).getType();
    }

    @Override
    public String getStringValue(String variableName) {
        if (constantMap.get(variableName) == null) {
            throw new IllegalArgumentException("Variable does not exist");
        }
        return constantMap.get(variableName).getStringValue();
    }

    @Override
    public double getDoubleValue(String variableName) {
        if (constantMap.get(variableName) == null) {
            throw new IllegalArgumentException("Variable does not exist");
        }
        return constantMap.get(variableName).getDoubleValue();
    }

    @Override
    public boolean getBooleanValue(String variableName) {
        if (constantMap.get(variableName) == null) {
            throw new IllegalArgumentException("Variable does not exist");
        }
        return constantMap.get(variableName).getBooleanValue();
    }

    @Override
    public Date getDateValue(String variableName) {
        if (constantMap.get(variableName) == null) {
            throw new IllegalArgumentException("Variable does not exist");
        }
        return constantMap.get(variableName).getDateValue();
    }

}
