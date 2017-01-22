package io.sugo.pio.tools.expression.internal.function.logical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

/**
 * Abstract class for a function that has arbitrary logical (numerical, true or false) inputs
 *
 * @author Sabrina Kirstein
 *
 */
public abstract class AbstractLogicalFunction extends AbstractFunction {

	/**
	 * Constructs a logical AbstractFunction with {@link FunctionDescription} generated from the
	 * arguments and the function name generated from the description.
	 *
	 * @param i18nKey
	 *            the key for the {@link FunctionDescription}. The functionName is read from
	 *            "gui.dialog.function.i18nKey.name", the helpTextName from ".help", the groupName
	 *            from ".group", the description from ".description" and the function with
	 *            parameters from ".parameters". If ".parameters" is not present, the ".name" is
	 *            taken for the function with parameters.
	 * @param numberOfArgumentsToCheck
	 *            the fixed number of parameters this functions expects or
	 *            {@link FunctionDescription#UNFIXED_NUMBER_OF_ARGUMENTS}
	 */
	public AbstractLogicalFunction(String i18nKey, int numberOfArgumentsToCheck) {
		super(i18nKey, numberOfArgumentsToCheck, Ontology.BINOMINAL);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		if (inputTypes.length > 2 || inputTypes.length < 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), "1", "2",
					inputTypes.length);
		}
		for (ExpressionType inputType : inputTypes) {
			if (inputType != ExpressionType.INTEGER && inputType != ExpressionType.DOUBLE
					&& inputType != ExpressionType.BOOLEAN) {
				throw new FunctionInputException("expression_parser.function_wrong_type_two", getFunctionName(), "boolean",
						"numerical");
			}
		}
		// result is always boolean
		return ExpressionType.BOOLEAN;
	}

}
