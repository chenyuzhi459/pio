package io.sugo.pio.tools.expression.internal.antlr;


import io.sugo.pio.tools.expression.*;

import java.util.Date;


/**
 * A basic {@link Expression}.
 *
 * @author Gisa Schaefer
 *
 */
class SimpleExpression implements Expression {

	private ExpressionEvaluator evaluator;

	/**
	 * Creates a basic expression based on the evaluator.
	 *
	 * @param evaluator
	 *            the evaluator to use for evaluating the expression
	 */
	SimpleExpression(ExpressionEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	@Override
	public ExpressionType getExpressionType() {
		return evaluator.getType();
	}

	@Override
	public Object evaluate() throws ExpressionException {
		try {
			switch (evaluator.getType()) {
				case DOUBLE:
				case INTEGER:
					return evaluator.getDoubleFunction().call();
				case BOOLEAN:
					Boolean booleanResult = evaluator.getBooleanFunction().call();
					return booleanResult == null ? UnknownValue.UNKNOWN_BOOLEAN : booleanResult;
				case DATE:
					Date dateResult = evaluator.getDateFunction().call();
					return dateResult == null ? UnknownValue.UNKNOWN_DATE : dateResult;
				case STRING:
				default:
					String stringResult = evaluator.getStringFunction().call();
					return stringResult == null ? UnknownValue.UNKNOWN_NOMINAL : stringResult;
			}
		} catch (ExpressionException e) {
			throw e;
		} catch (ExpressionParsingException e) {
			throw new ExpressionException(e);
		} catch (Exception e) {
			throw new ExpressionException(e.getLocalizedMessage());
		}
	}

	@Override
	public String evaluateNominal() throws ExpressionException {
		try {
			switch (evaluator.getType()) {
				case BOOLEAN:
					Boolean result = evaluator.getBooleanFunction().call();
					return result == null ? null : result.toString();
				case STRING:
					return evaluator.getStringFunction().call();
				default:
					throw new IllegalArgumentException("Cannot evaluate expression of type " + getExpressionType()
							+ " as nominal");
			}
		} catch (ExpressionException e) {
			throw e;
		} catch (ExpressionParsingException e) {
			throw new ExpressionException(e);
		} catch (Exception e) {
			throw new ExpressionException(e.getLocalizedMessage());
		}
	}

	@Override
	public double evaluateNumerical() throws ExpressionException {
		try {
			switch (evaluator.getType()) {
				case DOUBLE:
				case INTEGER:
					return evaluator.getDoubleFunction().call();
				default:
					throw new IllegalArgumentException("Cannot evaluate expression of type " + getExpressionType()
							+ " as numerical");
			}
		} catch (ExpressionException e) {
			throw e;
		} catch (ExpressionParsingException e) {
			throw new ExpressionException(e);
		} catch (Exception e) {
			throw new ExpressionException(e.getLocalizedMessage());
		}
	}

	@Override
	public Date evaluateDate() throws ExpressionException {
		try {
			switch (evaluator.getType()) {
				case DATE:
					return evaluator.getDateFunction().call();
				default:
					throw new IllegalArgumentException("Cannot evaluate expression of type " + getExpressionType()
							+ " as date");
			}
		} catch (ExpressionException e) {
			throw e;
		} catch (ExpressionParsingException e) {
			throw new ExpressionException(e);
		} catch (Exception e) {
			throw new ExpressionException(e.getLocalizedMessage());
		}
	}

	@Override
	public Boolean evaluateBoolean() throws ExpressionException {
		try {
			switch (evaluator.getType()) {
				case BOOLEAN:
					return evaluator.getBooleanFunction().call();
				default:
					throw new IllegalArgumentException("Cannot evaluate expression of type " + getExpressionType()
							+ " as boolean");
			}
		} catch (ExpressionException e) {
			throw e;
		} catch (ExpressionParsingException e) {
			throw new ExpressionException(e);
		} catch (Exception e) {
			throw new ExpressionException(e.getLocalizedMessage());
		}
	}

}
