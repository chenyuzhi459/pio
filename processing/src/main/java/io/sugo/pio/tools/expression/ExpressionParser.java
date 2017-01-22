package io.sugo.pio.tools.expression;


/**
 * Interface for an expression parser that can check the syntax of string expressions and parse
 * those into an {@link Expression}.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface ExpressionParser {

    /**
     * Parses the expression with the grammar and checks function names and number of arguments.
     *
     * @param expression the expression to parse
     * @throws ExpressionException if the syntax check failed. The ExpressionException can contain a
     *                             {@link ExpressionParsingException} as cause. see the java doc of
     *                             {@link ExpressionParsingException} for different marker subclasses of special
     *                             error cases.
     */
    public void checkSyntax(String expression) throws ExpressionException;

    /**
     * Parses the expression with the grammar and precompiles by evaluating constant parts.
     *
     * @param expression the expression to parse
     * @return the generated Expression
     * @throws ExpressionException if the parsing failed. The ExpressionException can contain a
     *                             {@link ExpressionParsingException} as cause. see the java doc of
     *                             {@link ExpressionParsingException} for different marker subclasses of special
     *                             error cases.
     */
    public Expression parse(String expression) throws ExpressionException;

    /**
     * Returns the {@link ExpressionContext} known by parser.
     *
     * @return the expression context
     */
    public ExpressionContext getExpressionContext();

}
