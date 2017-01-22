package io.sugo.pio.tools.expression;


import java.util.List;

/**
 * A module that can be used to construct an {@link ExpressionParser} via
 * {@link ExpressionParserBuilder#withModule(ExpressionParserModule)} or
 * {@link ExpressionParserBuilder#withModules(List)}.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface ExpressionParserModule {

	/**
	 * Returns the key associated to this module. The constants of this module are shown in the
	 * {@link ExpressionPropertyDialog} under the category defined by
	 * "gui.dialog.function_input.key.constant_category".
	 *
	 * @return the key for the module
	 */
	public String getKey();

	/**
	 * Returns all {@link Constant}s stored in this module. The constants are shown in the
	 * {@link ExpressionPropertyDialog} under the category defined by
	 * "gui.dialog.function_input.key.constant_category" where key is defined by {@link #getKey()}.
	 *
	 * @return all constants known by this module
	 */
	public List<Constant> getConstants();

	/**
	 * Returns all {@link Function}s stored in this module.
	 *
	 * @return all functions known by this module
	 */
	public List<Function> getFunctions();

}
