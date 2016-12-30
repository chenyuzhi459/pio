package io.sugo.pio.parameter;

import java.util.List;

/**
 */
public interface  ParameterHandler {
    /** Returns a collection of all parameters of this parameter handler. */
    Parameters getParameters();

    /** Returns a list of all defined parameter types for this handler. */
    List<ParameterType> getParameterTypes();

    String getParameter(String key);

    /**
     * Sets all parameters of this operator. The given parameters are not allowed to be null and
     * must correspond to the parameter types defined by this parameter handler.
     */
    void setParameters(Parameters parameters);

    /**
     * Sets the given single parameter to the Parameters object of this operator. For parameter list
     * the method {@link #setListParameter(String, List)} should be used.
     */
    void setParameter(String key, String value);

    /**
     * Sets the given parameter list to the Parameters object of this operator. For single
     * parameters the method {@link #setParameter(String, String)} should be used.
     */
    void setListParameter(String key, List<String[]> list);

    /** Returns a single named parameter and casts it to String. */
    String getParameterAsString(String key);

    /** Returns a single named parameter and casts it to char. */
    char getParameterAsChar(String key);

    /** Returns a single named parameter and casts it to int. */
    int getParameterAsInt(String key);

    /** Returns a single named parameter and casts it to double. */
    double getParameterAsDouble(String key);

    /** Returns a single named parameter and casts it to long. */
    long getParameterAsLong(String key);

    /**
     * Returns a single named parameter and casts it to boolean. This method never throws an
     * exception since there are no non-optional boolean parameters.
     */
    boolean getParameterAsBoolean(String key);

    /**
     * Returns a single named parameter and casts it to List. The list returned by this method
     * contains the user defined key-value pairs. Each element is an String array of length 2. The
     * first element is the key, the second the parameter value.
     */
    List<String[]> getParameterList(String key) throws UndefinedParameterError;
}
