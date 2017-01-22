package io.sugo.pio.tools.expression;



/**
 * Exception that happens while parsing a string expression or checking its syntax. Can contain a
 * {@link ExpressionParsingException} or a subclass determining the reason for the parsing error.
 * See {@link ExpressionParsingException} for standard marker subclasses.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public class ExpressionException extends Exception {

	private static final long serialVersionUID = 1566969757994992388L;
	private final int errorLine;

	/**
	 * Creates an ExpressionException with a line where the error happened and an associated error
	 * message.
	 *
	 * @param message
	 *            the error message
	 * @param line
	 *            the line where the error occurred
	 */
	public ExpressionException(String message, int line) {
		super(message);
		errorLine = line;
	}

	/**
	 * Creates an ExpressionException with associated error message and unknown line for where the
	 * error happened.
	 *
	 * @param message
	 *            the error message
	 */
	public ExpressionException(String message) {
		this(message, -1);
	}

	/**
	 * Creates an ExpressionException with the cause, associated error message and unknown line for
	 * where the error happened.
	 *
	 * @param cause
	 *            the cause
	 */
	public ExpressionException(ExpressionParsingException cause) {
		super(cause.getMessage(), cause);
		if (cause.getErrorContext() != null) {
			errorLine = cause.getErrorContext().getStart().getLine();
		} else {
			errorLine = -1;
		}
	}

	/**
	 * @return the line of the error or -1 if the error line is unknown
	 */
	public int getErrorLine() {
		return errorLine;

	}

	/**
	 * Returns only the first sentence of the error message. Does not return where exactly the error
	 * lies as {{@link #getMessage()} may.
	 *
	 * @return the first sentence of the error message
	 */
	public String getShortMessage() {
		return super.getMessage().split("\n")[0];
	}

}
