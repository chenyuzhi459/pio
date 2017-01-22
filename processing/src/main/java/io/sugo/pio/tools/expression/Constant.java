package io.sugo.pio.tools.expression;

import java.util.Date;


/**
 * Interface for a constant used inside an expression that is parsed by an {@link ExpressionParser}.
 *
 */
public interface Constant {

	/**
	 * @return the {@link ExpressionType}
	 */
	public ExpressionType getType();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the string value if the constant has type {@link ExpressionType#STRING}
	 * @throws IllegalStateException
	 *             if the type is not {@link ExpressionType#STRING}
	 */
	public String getStringValue();

	/**
	 * @return the double value if the constant has type {@link ExpressionType#DOUBLE}
	 * @throws IllegalStateException
	 *             if the type is not {@link ExpressionType#DOUBLE}
	 */
	public double getDoubleValue();

	/**
	 * @return the boolean value if the constant has type {@link ExpressionType#BOOLEAN}
	 * @throws IllegalStateException
	 *             if the type is not {@link ExpressionType#BOOLEAN}
	 */
	public boolean getBooleanValue();

	/**
	 * @return the Date value if the constant has type {@link ExpressionType#DATE}
	 * @throws IllegalStateException
	 *             if the type is not {@link ExpressionType#DATE}
	 */
	public Date getDateValue();

	/**
	 * Returns the annotation of this constant, for example a description of a constant or where it
	 * is used.
	 *
	 * @return the annotation
	 */
	public String getAnnotation();

	/**
	 * @return is this {@link Constant} should be visible in the UI
	 */
	public boolean isInvisible();
}
