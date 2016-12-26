package io.sugo.pio.parameter;

import java.util.List;

/**
 */
public interface  ParameterHandler {
    /** Returns a collection of all parameters of this parameter handler. */
    public Parameters getParameters();

    /** Returns a list of all defined parameter types for this handler. */
    public List<ParameterType> getParameterTypes();

    public String getParameter(String key);

    /**
     * Sets all parameters of this operator. The given parameters are not allowed to be null and
     * must correspond to the parameter types defined by this parameter handler.
     */
    public void setParameters(Parameters parameters);

    /**
     * Sets the given single parameter to the Parameters object of this operator. For parameter list
     * the method {@link #setListParameter(String, List)} should be used.
     */
    public void setParameter(String key, String value);

    /**
     * Sets the given parameter list to the Parameters object of this operator. For single
     * parameters the method {@link #setParameter(String, String)} should be used.
     */
    public void setListParameter(String key, List<String[]> list);

    /** Returns a single named parameter and casts it to String. */
    public String getParameterAsString(String key);

    /** Returns a single named parameter and casts it to char. */
    public char getParameterAsChar(String key);

    /** Returns a single named parameter and casts it to int. */
    public int getParameterAsInt(String key);

    /** Returns a single named parameter and casts it to double. */
    public double getParameterAsDouble(String key);

    /** Returns a single named parameter and casts it to long. */
    public long getParameterAsLong(String key);

    /**
     * Returns a single named parameter and casts it to boolean. This method never throws an
     * exception since there are no non-optional boolean parameters.
     */
    public boolean getParameterAsBoolean(String key);
}
