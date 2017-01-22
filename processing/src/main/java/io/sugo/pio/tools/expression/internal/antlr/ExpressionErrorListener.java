package io.sugo.pio.tools.expression.internal.antlr;

import org.antlr.v4.runtime.*;


/**
 * Listener for errors in the expression parser. Stores the error line and its message.
 *
 * @author Gisa Schaefer
 *
 */
class ExpressionErrorListener extends BaseErrorListener {

	private String errorMessage;
	private int line;

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
							String msg, RecognitionException e) {
		super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);

		this.line = line;

		if (recognizer.getInputStream() instanceof CommonTokenStream) {
			StringBuilder errorBuilder = new StringBuilder(msg);

			CommonTokenStream tokenStream = (CommonTokenStream) recognizer.getInputStream();
			errorBuilder.append("\n");

			String expression = tokenStream.getTokenSource().getInputStream().toString();
			String[] split = expression.split("(\r\n|\n)");
			String error = null;
			if (split.length > 0) {
				if (line - 1 >= 0 && line - 1 < split.length) {
					error = split[line - 1].replace("\t", " ");
				} else {
					error = split[split.length - 1].replace("\t", " ");
				}
				errorBuilder.append(error);
				errorBuilder.append("\n");
			} else {
				errorBuilder.append(expression);
				errorBuilder.append("\n");
			}

			for (int i = 0; i < charPositionInLine; i++) {
				errorBuilder.append(" ");
			}

			if (offendingSymbol instanceof Token) {
				Token token = (Token) offendingSymbol;
				int startIndex = token.getStartIndex();
				int stopIndex = token.getStopIndex();
				if (startIndex >= 0 && stopIndex >= 0 && startIndex <= stopIndex) {
					for (int i = token.getStartIndex(); i <= token.getStopIndex(); i++) {
						errorBuilder.append("^");
					}
				} else {
					errorBuilder.append("^");
				}
			} else {
				errorBuilder.append("^");
			}

			errorMessage = errorBuilder.toString();
		} else {
			errorMessage = msg;
		}
	}

	/**
	 * @return the message describing the syntax error
	 */
	String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the line containing the error, starting with 1
	 */
	int getErrorLine() {
		return line;
	}

	/**
	 * @return {@code true} if a syntax error occurred
	 */
	boolean containsError() {
		return errorMessage != null;
	}

}
