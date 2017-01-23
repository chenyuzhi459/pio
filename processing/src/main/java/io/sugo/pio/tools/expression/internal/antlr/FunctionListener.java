package io.sugo.pio.tools.expression.internal.antlr;


import io.sugo.pio.tools.expression.ExpressionContext;
import io.sugo.pio.tools.expression.Function;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.antlr.generated.FunctionExpressionParser.OperationExpContext;
import io.sugo.pio.tools.expression.internal.antlr.generated.FunctionExpressionParser.FunctionContext;
import io.sugo.pio.tools.expression.internal.antlr.generated.FunctionExpressionParserBaseListener;

/**
 * Antlr ParseTreeListener that checks if the operators in the expression are known, the functions
 * are valid functions and have the right number of arguments.
 *
 * @author Gisa Schaefer
 *
 */
class FunctionListener extends FunctionExpressionParserBaseListener {

	private ExpressionContext lookup;

	/**
	 * Creates a listener that checks if operators and function names in the expression are known by
	 * the lookup and have the right number of arguments.
	 *
	 * @param lookup
	 *            the ExpressionContext that administers the valid functions
	 */
	FunctionListener(ExpressionContext lookup) {
		this.lookup = lookup;
	}

	@Override
	public void enterOperationExp(OperationExpContext ctx) {
		if (ctx.op == null) {
			return;
		} else {
			String operatorName = ctx.op.getText();
			Function function = lookup.getFunction(ctx.op.getText());
			if (function == null) {
				throw new UnknownFunctionException(ctx, "expression_parser.unknown_operator", operatorName);
			}
		}
	}

	@Override
	public void enterFunction(FunctionContext ctx) {
		String functionName = ctx.NAME().getText();
		Function function = lookup.getFunction(functionName);
		if (function == null) {
			throw new UnknownFunctionException(ctx, "expression_parser.unknown_function", functionName);
		}
		int currentInputs = ctx.operationExp().size();
		int expectedInputs = function.getFunctionDescription().getNumberOfArguments();
		if (expectedInputs > FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS && currentInputs != expectedInputs) {
			throw new FunctionInputException(ctx, "expression_parser.function_wrong_input", function.getFunctionName(),
					expectedInputs, currentInputs);
		}
	}

}
