package io.sugo.pio.tools.expression.internal.antlr;

import io.sugo.pio.tools.expression.ExpressionParsingException;
import org.antlr.v4.runtime.ParserRuleContext;


/**
 * A {@link ExpressionParsingException} that is thrown when a function is unknown.
 *
 *
 * @author Gisa Schaefer
 *
 */
public class UnknownFunctionException extends ExpressionParsingException {

	private static final long serialVersionUID = -7352680996412482697L;

	/**
	 * Creates a parsing exception with message associated to the i18n and the arguments.
	 *
	 * @param i18n
	 *            the i18n error key
	 * @param arguments
	 */
	UnknownFunctionException(String i18n, Object... arguments) {
		super(i18n, arguments);
	}

	/**
	 * Creates a parsing exception with message associated to the i18n and the arguments and stores
	 * the error context ctx.
	 *
	 * @param ctx
	 *            the error context
	 * @param i18n
	 *            the i18n error key
	 * @param arguments
	 */
	UnknownFunctionException(ParserRuleContext ctx, String i18n, Object... arguments) {
		super(ctx, i18n, arguments);
	}

}
