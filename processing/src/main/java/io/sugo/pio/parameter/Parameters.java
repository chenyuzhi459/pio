package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 */
public class Parameters implements Cloneable, Iterable<String> {

    //    public static final char PAIR_SEPARATOR = '\u241D';
//    public static final char RECORD_SEPARATOR = '\u241E';
    public static final char PAIR_SEPARATOR = ':';
    public static final char RECORD_SEPARATOR = ';';

    /**
     * Maps parameter keys (i.e. Strings) to their value (Objects).
     */
    @JsonProperty
    private final Map<String, String> keyToValueMap = new LinkedHashMap<String, String>();

    /**
     * Maps parameter keys (i.e. Strings) to their <code>ParameterType</code>.
     */
    private final Map<String, ParameterType> keyToTypeMap = new LinkedHashMap<String, ParameterType>();

    /**
     * Creates an empty parameters object without any parameter types.
     */
    public Parameters() {
    }

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
    @JsonProperty
    public Collection<ParameterType> getParameterTypes() {
        return keyToTypeMap.values();
    }

    public void addParameterType(ParameterType type) {
        keyToTypeMap.put(type.getKey(), type);
    }

    /**
     * Performs a deep clone on this parameters object.
     */
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

    /**
     * Returns the type of the parameter with the given type.
     */
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

    /**
     * Returns the value of the parameter as specified by the process definition file (without
     * substituting default values etc.)
     */
    public String getParameterAsSpecified(String key) {
        return keyToValueMap.get(key);
    }

    /**
     * Sets the parameter for the given key after performing a range-check. This method returns true
     * if the type was known and false if no parameter type was defined for this key.
     */
    public boolean setParameter(String key, String value) {
        ParameterType parameterType = keyToTypeMap.get(key);
        if (value == null) {
            keyToValueMap.remove(key);
        } else {
            keyToValueMap.put(key, value);
        }
        return parameterType != null;
    }

    /**
     * Returns true if the parameter is set or has a default value.
     *
     * @see Parameters#isSpecified(String)
     */
    public boolean isSet(String parameterKey) {
        if (keyToValueMap.containsKey(parameterKey)) {
            return true;
        } else {
            // check for default if we have a type registered for this key
            ParameterType type = keyToTypeMap.get(parameterKey);
            if (type == null) {
                return false;
            }
            return type.getDefaultValue() != null;
        }
    }

    /**
     * Returns true iff the parameter value was explicitly set (as opposed to {@link #isSet(String)}
     * which also takes into account a possible default value.
     */
    public boolean isSpecified(String key) {
        return keyToValueMap.containsKey(key);
    }
}
