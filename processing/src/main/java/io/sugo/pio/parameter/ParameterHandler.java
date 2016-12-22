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
