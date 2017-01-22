package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.tools.ExpressionEvaluationException;
import io.sugo.pio.tools.expression.ExampleResolver;
import io.sugo.pio.tools.expression.Expression;
import io.sugo.pio.tools.expression.ExpressionException;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.internal.ExpressionParserUtils;

/**
 * The condition is fulfilled if the expression evaluates to <code>true</code>. Respectively the
 * condition is not fulfilled if the expression evaluates to <code>false</code> or <code>null</code>
 * .
 *
 * @author Marco Boeck
 */
public class ExpressionFilter implements Condition {

	private static final long serialVersionUID = -8663210021090219277L;

	private String expression;

	private ExampleResolver resolver;

	private ExpressionType type;

	private Expression result;

	/**
	 * Creates a new {@link ExpressionFilter} instance with the given expression. The expression is
	 * evaluated via the expression parser and examples are ok if the expression evaluates to
	 * <code>true</code>.
	 *
	 * @param exampleSet
	 * @param expression
	 * @param process
	 * @param compatibilityLevel
	 * @throws ExpressionException
	 */
	public ExpressionFilter(ExampleSet exampleSet, String expression, Operator operator) throws ExpressionException {
		if (exampleSet == null) {
			throw new IllegalArgumentException("exampleSet must not be null!");
		}
		if (expression == null) {
			throw new IllegalArgumentException("expression must not be null!");
		}
		if (operator == null) {
			throw new IllegalArgumentException("operator must not be null!");
		}

		this.expression = expression;
		this.resolver = new ExampleResolver(exampleSet);

		this.result = ExpressionParserUtils.createAllModulesParser(operator, resolver).parse(expression);
		this.type = result.getExpressionType();
	}

	/**
	 * The sole purpose of this constructor is to provide a constructor that matches the expected
	 * signature for the {@link ConditionedExampleSet} reflection invocation. However, this class
	 * cannot be instantiated by an ExampleSet and a String, so we <b>always</b> throw an
	 * {@link IllegalArgumentException} to signal this filter cannot be instantiated that way.
	 *
	 * @throws IllegalArgumentException
	 *             <b>ALWAYS THROWN!</b>
	 */
	@Deprecated
	public ExpressionFilter(ExampleSet exampleSet, String parameterString) throws IllegalArgumentException {
		throw new IllegalArgumentException("This condition cannot be instantiated this way!");
	}

	/**
	 * Since the condition cannot be altered after creation we can just return the condition object
	 * itself.
	 *
	 * @deprecated Conditions should not be able to be changed dynamically and hence there is no
	 *             need for a copy
	 */
	@Override
	@Deprecated
	public Condition duplicate() {
		return this;
	}

	@Override
	public String toString() {
		return expression;
	}

	/** Returns true if all conditions are fulfilled for the given example. */
	@Override
	public boolean conditionOk(Example e) throws ExpressionEvaluationException {
		try {
			resolver.bind(e);

			if (type == ExpressionType.BOOLEAN) {
				Boolean resultValue = result.evaluateBoolean();
				if (resultValue == null) {
					return false;
				}
				return resultValue;
			} else if (type == ExpressionType.DOUBLE) {
				double resultValue = result.evaluateNumerical();
				if (resultValue == 1d || resultValue == 0d) {
					return resultValue == 1d;
				}
			}
			throw new ExpressionEvaluationException("expression_filter.expression_not_boolean");
//			throw new ExpressionEvaluationException(
//					I18N.getMessageOrNull(I18N.getErrorBundle(), "expression_filter.expression_not_boolean", expression));
		} catch (ExpressionException e1) {
			// all parsing tries failed, show warning and return false
			throw new ExpressionEvaluationException("expression_filter.parser_parsing_failed");
//			throw new ExpressionEvaluationException(
//					I18N.getMessageOrNull(I18N.getErrorBundle(), "expression_filter.parser_parsing_failed", expression));
		} finally {
			// avoid memory leak
			resolver.unbind();
		}
	}

}
