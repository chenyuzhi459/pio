package io.sugo.pio.tools.expression;


import java.util.Collection;
import java.util.Date;

/**
 * Resolver for variables and scope constants used in expressions. Handles some variables, their
 * meta data and their values.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface Resolver {

    /**
     * Returns the {@link FunctionInput}s of all variables known to this resolver.
     *
     * @return the {@link FunctionInput}s of all known variables
     */
    Collection<FunctionInput> getAllVariables();

    /**
     * Returns the {@link ExpressionType} of the variable with name variableName or {@code null} if
     * this variable does not exist.
     *
     * @param variableName the name of the variable
     * @return the type of the variable variableName or {@code null}
     */
    ExpressionType getVariableType(String variableName);

    /**
     * Returns the String value of the variable variableName, if this variable has a String value.
     * Check the expression type of the variable using {@link #getExpressionType()} before calling
     * this method.
     *
     * @param variableName the name of the variable
     * @return the String value of the variable variableName, if this variable has a String value
     * @throws IllegalStateException if the variable is not of type {@link ExpressionType#STRING}
     */
    String getStringValue(String variableName);

    /**
     * Returns the double value of the variable variableName, if this variable has a double value.
     * Check the expression type of the variable using {@link #getExpressionType()} before calling
     * this method.
     *
     * @param variableName the name of the variable
     * @return the double value of the variable variableName, if this variable has a double value
     * @throws IllegalStateException if the variable is not of type {@link ExpressionType#INTEGER} or
     *                               {@link ExpressionType#DOUBLE}
     */
    double getDoubleValue(String variableName);

    /**
     * Returns the boolean value of the variable variableName, if this variable has a boolean value.
     * Check the expression type of the variable using {@link #getExpressionType()} before calling
     * this method.
     *
     * @param variableName the name of the variable
     * @return the boolean value of the variable variableName, if this variable has a boolean value
     * @throws IllegalStateException if the variable is not of type {@link ExpressionType#BOOLEAN}
     */
    boolean getBooleanValue(String variableName);

    /**
     * Returns the Date value of the variable variableName, if this variable has a Date value. Check
     * the expression type of the variable using {@link #getExpressionType()} before calling this
     * method.
     *
     * @param variableName the name of the variable
     * @return the Date value of the variable variableName, if this variable has a Date value
     * @throws IllegalStateException if the variable is not of type {@link ExpressionType#DATE}
     */
    Date getDateValue(String variableName);
}
