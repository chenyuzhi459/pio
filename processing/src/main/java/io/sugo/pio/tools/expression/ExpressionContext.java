package io.sugo.pio.tools.expression;

import java.util.List;


/**
 * Interface for a context to evaluate an expression. Stores all {@link Function}s and has access to
 * all variables, dynamic variables and scope constants that can be used during such an evaluation.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface ExpressionContext {

	/**
	 * Returns the function with name functionName if it exists or {@code null}.
	 *
	 * @param functionName
	 *            the name of the function
	 * @return the function with functionName or {@code null}
	 */
	public Function getFunction(String functionName);

	/**
	 * Returns the {@link ExpressionEvaluator} for the variable with name variableName if such a
	 * variable exists, or {@code null}. Note that a variable can be either a constant or can come
	 * from a {@link Resolver} that was added with
	 * {@link ExpressionParserBuilder#withDynamics(Resolver)}.
	 *
	 * @param variableName
	 *            the name of the variable
	 * @return the {@ExpressionEvaluator} for variableName or {@code null}
	 */
	public ExpressionEvaluator getVariable(String variableName);

	/**
	 * Returns the {@link ExpressionEvaluator} for the dynamic variable (for example an attribute)
	 * with name variableName if such a dynamic variable exists, or {@code null}.
	 *
	 * @param variableName
	 *            the name of the dynamic variable
	 * @return the {@ExpressionEvaluator} for variableName or {@code null}
	 */
	public ExpressionEvaluator getDynamicVariable(String variableName);

	/**
	 * Returns the {@link ExpressionEvaluator} for the scope constant with name scopeName if such a
	 * scope constant exists, or {@code null}.
	 *
	 * @param scopeName
	 *            the name of the scope constant
	 * @return the {@ExpressionEvaluator} for scopeName or {@code null}
	 */
	public ExpressionEvaluator getScopeConstant(String scopeName);

	/**
	 * Returns the content of the scope constant with name scopeName as String if such a scope
	 * constant exists, or {@code null}.
	 *
	 * @param scopeName
	 *            the name of the scope constant
	 * @return the String content of scopeName or {@code null}
	 */
	public String getScopeString(String scopeName);

	/**
	 * Returns a the {@link FunctionDescription} for all {@link Function}s known to the context.
	 *
	 * @return the function descriptions for all known functions
	 */
	public List<FunctionDescription> getFunctionDescriptions();

	/**
	 * Returns the {@link FunctionInput}s associated to all attributes, variables and macros known
	 * by the context.
	 *
	 * @return all known function inputs
	 */
	public List<FunctionInput> getFunctionInputs();

	/**
	 * Returns the {@link ExpressionEvaluator} for the constant with name constantName if such a
	 * constant exists, or {@code null}.
	 *
	 * @param constantName
	 *            the name of the constant
	 * @return the {@ExpressionEvaluator} for constantName or {@code null}
	 */
	public ExpressionEvaluator getConstant(String constantName);

}
