package io.sugo.pio.tools.expression.internal.function;


import io.sugo.pio.tools.expression.ExpressionEvaluator;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.Function;
import io.sugo.pio.tools.expression.FunctionDescription;

/**
 * Abstract {@link Function} that provides a constructor with i18n and helper methods to compute the
 * result type and if the result is constant.
 *
 * @author Gisa Schaefer
 *
 */
public abstract class AbstractFunction implements Function {

	private final String functionName;
	private final FunctionDescription description;

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 *
	 * @param i18nKey
	 *            the key for the {@link FunctionDescription}. The functionName is read from
	 *            "gui.dialog.function.i18nKey.name", the helpTextName from ".help", the groupName
	 *            from ".group", the description from ".description" and the function with
	 *            parameters from ".parameters". If ".parameters" is not present, the ".name" is
	 *            taken for the function with parameters.
	 * @param numberOfArgumentsToCheck
	 *            the fixed number of parameters this functions expects or -1
	 * @param returnType
	 *            the {@link Ontology#ATTRIBUTE_VALUE_TYPE}
	 */
	public AbstractFunction(String i18nKey, int numberOfArgumentsToCheck, int returnType) {
		this.description = new FunctionDescription(i18nKey, numberOfArgumentsToCheck, returnType);
		functionName = description.getDisplayName().split("\\(")[0];
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public FunctionDescription getFunctionDescription() {
		return description;
	}

	/**
	 * Whether this function returns the same result for the same input. Default implementation
	 * returns {@code true}.
	 *
	 * @return default implementation returns {@code true}
	 */
	protected boolean isConstantOnConstantInput() {
		return true;
	}

	/**
	 * Computes the result {@ExpressionType} from the types of the arguments
	 * inputTypes.
	 *
	 * @param inputTypes
	 *            the types of the inputs
	 * @return the result type
	 */
	protected abstract ExpressionType computeType(ExpressionType... inputTypes);

	/**
	 * Extracts the {@link ExpressionType}s from the inputEvaluators and calls
	 * {@link #computeType(ExpressionType...)}.
	 *
	 * @param inputEvaluators
	 *            the input evaluators
	 * @return the result type of the function when getting the given inputs
	 */
	protected ExpressionType getResultType(ExpressionEvaluator... inputEvaluators) {
		ExpressionType[] inputTypes = new ExpressionType[inputEvaluators.length];
		for (int i = 0; i < inputEvaluators.length; i++) {
			inputTypes[i] = inputEvaluators[i].getType();
		}
		return computeType(inputTypes);
	}

	/**
	 * Computes whether the result of this function is constant.
	 *
	 * @param inputEvaluators
	 *            the input arguments
	 * @return {@code true} if the result of this function is constant
	 */
	protected boolean isResultConstant(ExpressionEvaluator... inputEvaluators) {
		boolean isConstant = isConstantOnConstantInput();
		for (ExpressionEvaluator input : inputEvaluators) {
			isConstant = isConstant && input.isConstant();
		}
		return isConstant;
	}

}
