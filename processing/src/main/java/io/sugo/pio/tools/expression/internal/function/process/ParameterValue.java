package io.sugo.pio.tools.expression.internal.function.process;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * Function for getting a parameter of a certain operator.
 *
 * @author Gisa Schaefer
 *
 */
public class ParameterValue extends AbstractArbitraryStringInputStringOutputFunction {

	private final OperatorProcess process;

	/**
	 * Creates a function that looks up a operator parameter in the process.
	 *
	 * @param process
	 *            the process where to find the operator
	 */
	public ParameterValue(OperatorProcess process) {
		super("process.param", 2);
		this.process = process;
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		int expectedInput = getFunctionDescription().getNumberOfArguments();
		if (length != expectedInput) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), expectedInput,
					length);
		}
	}

	@Override
	protected String compute(String... values) {
		try {
			Operator operator = process.getOperator(values[0]);
			if (operator == null) {
				throw new FunctionInputException("expression_parser.parameter_value_wrong_operator", getFunctionName());
			}
			return operator.getParameter(values[1]);
		} catch (UndefinedParameterError e) {
			throw new FunctionInputException("expression_parser.parameter_value_wrong_parameter", getFunctionName());
		}
	}

}
