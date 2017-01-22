package io.sugo.pio.tools.expression.internal.function.eval;


import io.sugo.pio.tools.expression.ExpressionParsingException;

/**
 * A {@link ExpressionParsingException} that is thrown when the function {@link Evaluation} fails to
 * evaluate a subexpression. Contains the causing {@link ExpressionParsingException} or
 * {@link ExpressionException} as cause.
 *
 * @author Gisa Schaefer
 */
public class SubexpressionEvaluationException extends ExpressionParsingException {

    private static final long serialVersionUID = -7644715146686931281L;

    /**
     * Creates a {@link SubexpressionEvaluationException} with the given cause and a message
     * generated from functionName, subExpression and the message of the cause.
     *
     * @param functionName  the name of the {@link Evaluation} function
     * @param subExpression the subexpression for which the {@link Evaluation} function failed
     * @param cause         the cause of the failure
     */
    SubexpressionEvaluationException(String functionName, String subExpression, Exception cause) {
        super("expression_parser.eval_failed" + functionName + subExpression + cause.getMessage(), cause);
    }

}
