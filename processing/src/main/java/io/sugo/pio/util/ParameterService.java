package io.sugo.pio.util;

import io.sugo.pio.util.parameter.Parameter;

import java.util.Map;
import java.util.TreeMap;

/**
 */
public class ParameterService {

    private static final Map<String, Parameter> PARAMETER_MAP = new TreeMap<>();

    /**
     * This method returns the value of the given parameter or null if this parameter is unknown.
     * For compatibility reasons this will return defined parameters as well as undefined.
     */
    public static String getParameterValue(String key) {
        Parameter parameter = PARAMETER_MAP.get(key);
        if (parameter != null) {
            return parameter.getValue();
        }
        return null;
    }
}
