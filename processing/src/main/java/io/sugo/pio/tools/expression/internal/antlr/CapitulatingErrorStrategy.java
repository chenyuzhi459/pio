package io.sugo.pio.tools.expression.internal.antlr;

import org.antlr.v4.runtime.*;


/**
 * Error strategy that does not recover after encountering an unrecognizable symbol and adjusts the
 * error messages of the {@link DefaultErrorStrategy}.
 *
 * @author Gisa Schaefer
 *
 */
class CapitulatingErrorStrategy extends DefaultErrorStrategy {

	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		// stop parsing when encountering the first error
		throw new CapitulatingRuntimeException();
	}

	@Override
	protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
		// change error message from default implementation
		TokenStream tokens = recognizer.getInputStream();
		String input;
		if (tokens != null) {
			if (e.getStartToken().getType() == Token.EOF) {
				input = "the end";
			} else {
				input = escapeWSAndQuote(tokens.getText(e.getStartToken(), e.getOffendingToken()));
			}
		} else {
			input = escapeWSAndQuote("<unknown input>");
		}
		String msg = "inadmissible input at " + input;
		recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
	}

	@Override
	protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
		// change error message from default implementation
		String msg = "mismatched input " + getTokenErrorDisplay(e.getOffendingToken()) + " expecting operator";
		recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
	}

	@Override
	protected void reportUnwantedToken(Parser recognizer) {
		// change error message from default implementation
		if (inErrorRecoveryMode(recognizer)) {
			return;
		}

		beginErrorCondition(recognizer);

		Token t = recognizer.getCurrentToken();
		String tokenName = getTokenErrorDisplay(t);
		String msg = "extraneous input " + tokenName + " expecting operator";
		recognizer.notifyErrorListeners(t, msg, null);
	}

	@Override
	protected String getTokenErrorDisplay(Token t) {
		// overwrite standard behavior to use "the end" instead of <EOF>
		if (t == null) {
			return "<no token>";
		}
		String s = getSymbolText(t).replace("<EOF>", "the end");
		if (s == null) {
			if (getSymbolType(t) == Token.EOF) {
				s = "the end";
			} else {
				s = escapeWSAndQuote("<" + getSymbolType(t) + ">");
			}
		}
		return s;
	}

}
