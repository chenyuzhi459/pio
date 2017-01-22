package io.sugo.pio.tools.expression.internal.antlr;

/**
 * {@link RuntimeException} thrown when the {@link CapitulatingFunctionExpressionLexer} or the
 * {@link CapitulatingErrorStrategy} encounters an error it does not want to recover from.
 *
 * @author Gisa Schaefer
 *
 */
class CapitulatingRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -4281221436108519452L;

	/**
	 * Creates a {@link RuntimeException} that marks that a parser or lexer has encountered an error
	 * it does not want to recover from.
	 */
	CapitulatingRuntimeException() {
		super();
	}

}
