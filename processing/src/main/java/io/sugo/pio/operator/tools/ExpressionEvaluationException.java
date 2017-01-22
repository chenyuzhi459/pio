package io.sugo.pio.operator.tools;


import io.sugo.pio.operator.OperatorException;

/**
 * This exception indicates an error during the evaluation of expressions.
 * 
 * @author Marco Boeck
 */
public class ExpressionEvaluationException extends OperatorException {

	private static final long serialVersionUID = 2654691902442722376L;

	public ExpressionEvaluationException(String str) {
		super(str);
	}

	public ExpressionEvaluationException(String str, Exception e) {
		super(str, e);
	}

}
