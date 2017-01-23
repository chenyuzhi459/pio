package io.sugo.pio.tools.expression.internal.antlr;

import io.sugo.pio.tools.expression.internal.antlr.generated.FunctionExpressionLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;


/**
 * {@link FunctionExpressionLexer} that does not try to recover after encountering the first
 * inadmissible symbol.
 *
 * @author Gisa Schaefer
 *
 */
class CapitulatingFunctionExpressionLexer extends FunctionExpressionLexer {

	CapitulatingFunctionExpressionLexer(CharStream input) {
		super(input);
	}

	@Override
	public void recover(LexerNoViableAltException e) {
		throw new CapitulatingRuntimeException();
	}

	@Override
	public void recover(RecognitionException re) {
		throw new CapitulatingRuntimeException();
	}

}
