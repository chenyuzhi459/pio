package io.sugo.pio.tools.expression.internal.antlr.generated;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;


/**
 * This class provides an empty implementation of {@link FunctionExpressionParserVisitor}, which can
 * be extended to create a visitor which only needs to handle a subset of the available methods.
 *
 * @param <T>
 *            The return type of the visit operation. Use {@link Void} for operations with no return
 *            type.
 */
public class FunctionExpressionParserBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements
		FunctionExpressionParserVisitor<T> {

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitOperationExp(FunctionExpressionParser.OperationExpContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitAtomExp(FunctionExpressionParser.AtomExpContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitLowerExp(FunctionExpressionParser.LowerExpContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitFunction(FunctionExpressionParser.FunctionContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitAttribute(FunctionExpressionParser.AttributeContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitScopeConstant(FunctionExpressionParser.ScopeConstantContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitIndirectScopeConstant(FunctionExpressionParser.IndirectScopeConstantContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitString(FunctionExpressionParser.StringContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitVariable(FunctionExpressionParser.VariableContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitReal(FunctionExpressionParser.RealContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling {@link #visitChildren} on
	 * {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitInteger(FunctionExpressionParser.IntegerContext ctx) {
		return visitChildren(ctx);
	}
}
