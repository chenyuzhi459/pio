package io.sugo.pio.tools.expression.internal;

import io.sugo.pio.tools.expression.ExpressionParsingException;
import org.antlr.v4.runtime.ParserRuleContext;


/**
 * A {@link ExpressionParsingException} that is thrown in case resolving a variable via a resolver
 * fails.
 *
 * @author Nils Woehler
 *
 */
public class UnknownResolverVariableException extends ExpressionParsingException {

	private static final long serialVersionUID = -9128291928735582300L;

	/**
	 * Creates a parsing exception with message associated to the i18n and the arguments.
	 *
	 * @param i18n
	 *            the i18n error key
	 * @param arguments
	 */
	protected UnknownResolverVariableException(String i18n, Object... arguments) {
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
	protected UnknownResolverVariableException(ParserRuleContext ctx, String i18n, Object... arguments) {
		super(ctx, i18n, arguments);
	}

}
