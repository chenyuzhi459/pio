package io.sugo.pio.tools.expression.internal.antlr;

import io.sugo.pio.tools.expression.internal.UnknownResolverVariableException;
import org.antlr.v4.runtime.ParserRuleContext;


/**
 *
 * A {@link ExpressionParsingException} that is thrown when a scope constant (used as '%{const}' or
 * '#{const}' in the expression) is unknown.
 *
 * @author Gisa Schaefer
 *
 */
public class UnknownScopeConstantException extends UnknownResolverVariableException {

	private static final long serialVersionUID = 6623843052999947562L;

	/**
	 * Creates a parsing exception with message associated to the i18n and the arguments.
	 *
	 * @param i18n
	 *            the i18n error key
	 * @param arguments
	 */
	UnknownScopeConstantException(String i18n, Object... arguments) {
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
	UnknownScopeConstantException(ParserRuleContext ctx, String i18n, Object... arguments) {
		super(ctx, i18n, arguments);
	}
}
