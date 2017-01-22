package io.sugo.pio.tools.expression;

/**
 * Class for a function that can be used inside expressions. Functions are contained in a
 * {@link ExpressionParserModule} which can be used in a {@link ExpressionParserBuilder} via
 * {@link ExpressionParserBuilder#withModule(ExpressionParserModule)} or
 * {@link ExpressionParserBuilder#withModules(java.util.List)}. Furthermore, the module containing
 * the {@link Function}s can be registered with the {@link ExpressionRegistry} such that the
 * functions are used in the standard core operators that use an {@link ExpressionParser}.
 *
 */
public interface Function {

	/**
	 * @return the {@link FunctionDescription} of the function
	 */
	public FunctionDescription getFunctionDescription();

	/**
	 * @return the function name of the function
	 */
	public String getFunctionName();

	/**
	 * Creates an {@link ExpressionEvaluator} for this function with the given inputEvaluators as
	 * arguments.
	 *
	 * @param inputEvaluators
	 *            the {@link ExpressionEvaluators} containing the input arguments
	 * @return an expression evaluator for this function applied to the inputEvaluators
	 * @throws ExpressionParsingException
	 *             if the creation of the ExpressionEvaluator fails, FunctionInputException if the
	 *             cause for the failure is a wrong argument
	 */
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) throws ExpressionParsingException;

}
