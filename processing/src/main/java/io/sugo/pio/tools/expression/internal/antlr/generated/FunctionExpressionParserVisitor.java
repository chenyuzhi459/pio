package io.sugo.pio.tools.expression.internal.antlr.generated;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;


/**
 * This interface defines a complete generic visitor for a parse tree produced by
 * {@link FunctionExpressionParser}.
 *
 * @param <T>
 *            The return type of the visit operation. Use {@link Void} for operations with no return
 *            type.
 */
public interface FunctionExpressionParserVisitor<T> extends ParseTreeVisitor<T> {

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#operationExp}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitOperationExp(FunctionExpressionParser.OperationExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#atomExp}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitAtomExp(FunctionExpressionParser.AtomExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#lowerExp}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitLowerExp(FunctionExpressionParser.LowerExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#function}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitFunction(FunctionExpressionParser.FunctionContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#attribute}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(FunctionExpressionParser.AttributeContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#scopeConstant}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitScopeConstant(FunctionExpressionParser.ScopeConstantContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#indirectScopeConstant}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitIndirectScopeConstant(FunctionExpressionParser.IndirectScopeConstantContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#string}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitString(FunctionExpressionParser.StringContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#variable}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitVariable(FunctionExpressionParser.VariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#real}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitReal(FunctionExpressionParser.RealContext ctx);

	/**
	 * Visit a parse tree produced by {@link FunctionExpressionParser#integer}.
	 *
	 * @param ctx
	 *            the parse tree
	 * @return the visitor result
	 */
	T visitInteger(FunctionExpressionParser.IntegerContext ctx);
}
