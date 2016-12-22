package io.sugo.pio.parameter;

import java.util.*;

/**
 */
public class Parameters implements Cloneable, Iterable<String> {

    /** Maps parameter keys (i.e. Strings) to their value (Objects). */
    private final Map<String, String> keyToValueMap = new LinkedHashMap<String, String>();

    /** Maps parameter keys (i.e. Strings) to their <code>ParameterType</code>. */
    private final Map<String, ParameterType> keyToTypeMap = new LinkedHashMap<String, ParameterType>();

    /** Creates an empty parameters object without any parameter types. */
    public Parameters() {}

    /**
     * Constructs an instance of <code>Parameters</code> for the given list of
     * <code>ParameterTypes</code>. The list might be empty but not null.
     */
    public Parameters(List<ParameterType> parameterTypes) {
        for (ParameterType type : parameterTypes) {
            addParameterType(type);
        }
    }

    /**
     * Returns a list of <tt>ParameterTypes</tt> describing the parameters of this operator. This
     * list will be generated during construction time of the Operator.
     */
    public Collection<ParameterType> getParameterTypes() {
        return keyToTypeMap.values();
    }

    public void addParameterType(ParameterType type) {
        keyToTypeMap.put(type.getKey(), type);
    }

    /** Performs a deep clone on this parameters object. */
    @Override
    public Object clone() {
        Parameters clone = new Parameters();

        Iterator<String> i = keyToValueMap.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            String value = keyToValueMap.get(key);
            ParameterType type = keyToTypeMap.get(key);
            if (type != null) {
                clone.keyToValueMap.put(key, value);
            }
        }
        i = keyToTypeMap.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            clone.keyToTypeMap.put(key, keyToTypeMap.get(key));
        }

        return clone;
    }

    @Override
    public Iterator<String> iterator() {
        return keyToTypeMap.keySet().iterator();
    }

    /** Returns the type of the parameter with the given type. */
    public ParameterType getParameterType(String key) {
        return keyToTypeMap.get(key);
    }

    /**
     * Returns the value of the given parameter. If it was not yet set, the default value is set now
     * and a log message is issued. If the <code>ParameterType</code> does not provide a default
     * value, this may result in an error message. In subsequent calls of this method, the parameter
     * will be set. An OperatorException (UserError) will be thrown if a non-optional parameter was
     * not set.
     */
    public String getParameter(String key) {
        if (keyToValueMap.containsKey(key)) {
            return keyToValueMap.get(key);
        } else {
            ParameterType type = keyToTypeMap.get(key);
            if (type == null) {
                return null;
            }
            Object defaultValue = type.getDefaultValue();
            if ((defaultValue == null) && !type.isOptional()) {
                //TODO: Maybe change to a checked exception
                throw new RuntimeException("");
            }
            if (defaultValue == null) {
                return null;
            } else {
                return type.toString(defaultValue);
            }
        }
    }
}
