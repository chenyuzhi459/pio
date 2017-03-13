package io.sugo.pio.parameter;

import io.sugo.pio.operator.UserError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 */
public interface  ParameterHandler {
    /**
     * string expansion key which will be replaced with the current system date and time
     */
    public static final String STRING_EXPANSION_MACRO_TIME = "t";
    /**
     * string expansion key which will be replaced with the number of times the operator was
     * applied.
     */
    public static final String STRING_EXPANSION_MACRO_NUMBER_APPLIED_TIMES_USER_FRIENDLY = "execution_count";
    /**
     * string expansion key which will be replaced with the name of the operator.
     */
    public static final String STRING_EXPANSION_MACRO_OPERATORNAME_USER_FRIENDLY = "operator_name";
    /**
     * string expansion key which will be replaced with the name of the operator.
     */
    public static final String STRING_EXPANSION_MACRO_OPERATORNAME = "n";
    /**
     * string expansion key which will be replaced with the class of the operator.
     */
    public static final String STRING_EXPANSION_MACRO_OPERATORCLASS = "c";
    /**
     * string expansion key which will be replaced with the number of times the operator was
     * applied.
     */
    public static final String STRING_EXPANSION_MACRO_NUMBER_APPLIED_TIMES = "a";
    /**
     * string expansion key which will be replaced with the number of times the operator was applied
     * plus one (a shortcut for %{p[1]}).
     */
    public static final String STRING_EXPANSION_MACRO_NUMBER_APPLIED_TIMES_PLUS_ONE = "b";
    /**
     * string expansion key which will be replaced with %.
     */
    public static final String STRING_EXPANSION_MACRO_PERCENT_SIGN = "%";
    /**
     * string expansion key which will be replaced with the number of times the operator was applied
     * plus the specified number.
     */
    public static final String STRING_EXPANSION_MACRO_NUMBER_APPLIED_TIMES_SHIFTED = "p";
    /**
     * indicates the the start of a string expansion parameter
     */
    public static final String STRING_EXPANSION_MACRO_PARAMETER_START = "[";
    /**
     * indicates the the end of a string expansion parameter
     */
    public static final String STRING_EXPANSION_MACRO_PARAMETER_END = "]";
    /**
     * string expansion key which will be replaced with the specified value of the operator with the
     * specified name.
     */
    public static final String STRING_EXPANSION_MACRO_OPERATORVALUE = "v";
    /**
     * indicates the start of a macro
     */
    public static final String MACRO_STRING_START = "%{";
    /**
     * indicates the end of a macro
     */
    public static final String MACRO_STRING_END = "}";
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

    /** Returns true iff the parameter with the given name is set. */
    public boolean isParameterSet(String key) throws UndefinedParameterError;

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
     * Returns a single named parameter and casts it to File. This file is already resolved against
     * the process definition file. If the parameter name defines a non-optional parameter which is
     * not set and has no default value, a UndefinedParameterError will be thrown. If the parameter
     * is optional and was not set this method returns null. Operators should always use this method
     * instead of directly using the method {@link Process#resolveFileName(String)}.
     *
     * @throws DirectoryCreationError
     * @throws UserError
     */
    public java.io.File getParameterAsFile(String key) throws UserError;

    /**
     * Returns a single named parameter and casts it to List. The list returned by this method
     * contains the user defined key-value pairs. Each element is an String array of length 2. The
     * first element is the key, the second the parameter value.
     */
    List<String[]> getParameterList(String key) throws UndefinedParameterError;
}
