package io.sugo.pio.tools.expression;


import java.util.Date;

/**
 * Interface for an expression generated by an {@link ExpressionParser}.
 *
 */
public interface Expression {

	/**
	 * Returns the {@link ExpressionType} of the expression. The expression type indicates the
	 * result type when evaluating the expression.
	 *
	 * @return the expression type of the expression
	 */
	public ExpressionType getExpressionType();

	/**
	 * Returns the result of the evaluation as object. Returns an {@link UnknownValue} when a
	 * String, Date or Boolean result is {@code null} indicating a missing value.
	 *
	 * @return the result of evaluating the expression, can be a {@link UnknownValue}
	 * @throws ExpressionException
	 *             if the evaluation failed. The ExpressionException can contain a
	 *             {@link ExpressionParsingException} as cause. See the java doc of
	 *             {@link ExpressionParsingException} for different marker subclasses of special
	 *             error cases.
	 */
	public Object evaluate() throws ExpressionException;

	/**
	 * If the type of the expression is {@link ExpressionType#STRING} or
	 * {@link ExpressionType#BOOLEAN} returns the String result of the evaluation. The return value
	 * can be {@code null} indicating a missing nominal value. Check the expression type using
	 * {@link #getExpressionType()} before calling this method.
	 *
	 * @return the nominal result of the evaluation if the expression is nominal, can return
	 *         {@code null}
	 * @throws ExpressionException
	 *             if the evaluation failed. The ExpressionException can contain a
	 *             {@link ExpressionParsingException} as cause. see the java doc of
	 *             {@link ExpressionParsingException} for different marker subclasses of special
	 *             error cases.
	 */
	public String evaluateNominal() throws ExpressionException;

	/**
	 * If the type of the expression is {@link ExpressionType#DOUBLE} or
	 * {@link ExpressionType#INTEGER} returns the double result of the evaluation. Check the
	 * expression type using {@link #getExpressionType()} before calling this method.
	 *
	 * @return the numerical result of the evaluation if the expression is numerical
	 * @throws ExpressionException
	 *             if the evaluation failed. The ExpressionException can contain a
	 *             {@link ExpressionParsingException} as cause. see the java doc of
	 *             {@link ExpressionParsingException} for different marker subclasses of special
	 *             error cases.
	 */
	public double evaluateNumerical() throws ExpressionException;

	/**
	 * If the type of the expression is {@link ExpressionType#DATE} returns the Date result of the
	 * evaluation. The return value can be {@code null} indicating a missing date value. Check the
	 * expression type using {@link #getExpressionType()} before calling this method.
	 *
	 * @return the date result of the evaluation if the expression is a date expression, can return
	 *         {@code null}
	 * @throws ExpressionException
	 *             if the evaluation failed. The ExpressionException can contain a
	 *             {@link ExpressionParsingException} as cause. see the java doc of
	 *             {@link ExpressionParsingException} for different marker subclasses of special
	 *             error cases.
	 */
	public Date evaluateDate() throws ExpressionException;

	/**
	 * If the type of the expression is {@link ExpressionType#BOOLEAN} returns the Boolean result of
	 * the evaluation. The return value can be {@code null} indicating a missing value. Check the
	 * expression type using {@link #getExpressionType()} before calling this method.
	 *
	 * @return the Boolean result of the evaluation if the expression is nominal, can return
	 *         {@code null}
	 * @throws ExpressionException
	 *             if the evaluation failed. The ExpressionException can contain a
	 *             {@link ExpressionParsingException} as cause. see the java doc of
	 *             {@link ExpressionParsingException} for different marker subclasses of special
	 *             error cases.
	 */
	public Boolean evaluateBoolean() throws ExpressionException;
}
