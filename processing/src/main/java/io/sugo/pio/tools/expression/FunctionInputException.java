package io.sugo.pio.tools.expression;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * A {@link ExpressionParsingException} that is thrown when a {@link Function} has wrong arguments.
 */
public class FunctionInputException extends ExpressionParsingException {

    private static final long serialVersionUID = 4495628675895791328L;

    /**
     * Wraps an exception into an {@link ExpressionParsingException} and takes its message.
     *
     * @param e the throwable that is the cause
     */
    public FunctionInputException(Throwable e) {
        super(e);
    }

    /**
     * Creates a parsing exception with message associated to the i18n and the arguments.
     *
     * @param i18n      the i18n error key
     * @param arguments
     */
    public FunctionInputException(String i18n, Object... arguments) {
        super(i18n, arguments);
    }

    /**
     * Creates a parsing exception with message associated to the i18n and the arguments and stores
     * the error context ctx.
     *
     * @param ctx       the error context
     * @param i18n      the i18n error key
     * @param arguments
     */
    public FunctionInputException(ParserRuleContext ctx, String i18n, Object... arguments) {
        super(ctx, i18n, arguments);
    }

}
