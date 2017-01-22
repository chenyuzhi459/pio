package io.sugo.pio.tools.expression;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.tools.expression.internal.ConstantResolver;
import io.sugo.pio.tools.expression.internal.SimpleExpressionContext;
import io.sugo.pio.tools.expression.internal.antlr.AntlrParser;
import io.sugo.pio.tools.expression.internal.function.eval.Evaluation;
import io.sugo.pio.tools.expression.internal.function.eval.TypeConstants;
import io.sugo.pio.tools.expression.internal.function.process.MacroValue;
import io.sugo.pio.tools.expression.internal.function.process.ParameterValue;
import io.sugo.pio.tools.expression.internal.function.statistical.Random;

import java.util.LinkedList;
import java.util.List;

/**
 * Builder for an {@link ExpressionParser}.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public class ExpressionParserBuilder {

	/**
	 * The last version which contained the old expression parser with different functions and
	 * different Macro handling.
	 */
	public static final OperatorVersion OLD_EXPRESSION_PARSER_FUNCTIONS = new OperatorVersion(6, 4, 0);

	private OperatorProcess process;
	private boolean compatibleWithOldParser;

	private List<Function> functions = new LinkedList<>();

	private List<Resolver> scopeResolvers = new LinkedList<>();
	private List<Resolver> dynamicsResolvers = new LinkedList<>();
	private List<Resolver> constantResolvers = new LinkedList<>();

	/**
	 * Builds an {@link ExpressionParser} with the given data.
	 *
	 * @return an expression parser
	 */
	public ExpressionParser build() {
		// add functions with process information
		if (process != null) {
			functions.add(new Random(process));
			functions.add(new ParameterValue(process));
			if (compatibleWithOldParser) {
				functions.add(new MacroValue(process));
			}
		}

		Evaluation evalFunction = null;
		if (!compatibleWithOldParser) {
			// add the eval function, always present except when in compatibility mode with old
			// parser
			evalFunction = new Evaluation();
			functions.add(evalFunction);
		}

		// add eval constants
		constantResolvers.add(new ConstantResolver(TypeConstants.INSTANCE.getKey(), TypeConstants.INSTANCE.getConstants()));

		ExpressionContext context = new SimpleExpressionContext(functions, scopeResolvers, dynamicsResolvers,
				constantResolvers);
		AntlrParser parser = new AntlrParser(context);

		if (!compatibleWithOldParser) {
			// set parser for eval function
			evalFunction.setParser(parser);
		}

		return parser;
	}

	/**
	 * Adds the process which enables process dependent functions.
	 *
	 * @param process
	 *            the process to add
	 * @return the builder
	 */
	public ExpressionParserBuilder withProcess(OperatorProcess process) {
		this.process = process;
		return this;
	}

	/**
	 * Adds the resolver as a resolver for scope constants (%{scope_constant} in the expression).
	 *
	 * @param resolver
	 *            the resolver to add
	 * @return the builder
	 */
	public ExpressionParserBuilder withScope(Resolver resolver) {
		scopeResolvers.add(resolver);
		return this;
	}

	/**
	 * Adds the resolver as a resolver for dynamic variables ([variable_name] or variable_name in
	 * the expression).
	 *
	 * @param resolver
	 *            the resolver to add
	 * @return the builder
	 */
	public ExpressionParserBuilder withDynamics(Resolver resolver) {
		dynamicsResolvers.add(resolver);
		return this;
	}

	/**
	 * Adds the given module that supplies functions and constant values.
	 *
	 * @param module
	 *            the module to add
	 * @return the builder
	 */
	public ExpressionParserBuilder withModule(ExpressionParserModule module) {
		addModule(module);
		return this;
	}

	/**
	 * Adds all functions of the module to the list of functions and adds a {@link ConstantResolver}
	 * knowing all constants of the module to the list of constant resolver.
	 *
	 * @param module
	 */
	private void addModule(ExpressionParserModule module) {
		List<Constant> moduleConstants = module.getConstants();
		if (moduleConstants != null && !moduleConstants.isEmpty()) {
			constantResolvers.add(new ConstantResolver(module.getKey(), moduleConstants));
		}
		List<Function> moduleFunctions = module.getFunctions();
		if (moduleFunctions != null) {
			functions.addAll(moduleFunctions);
		}
	}

	/**
	 * Adds the given modules that supplies functions and constant values.
	 *
	 * @param modules
	 *            the modules to add
	 * @return the builder
	 */
	public ExpressionParserBuilder withModules(List<ExpressionParserModule> modules) {
		for (ExpressionParserModule module : modules) {
			addModule(module);
		}
		return this;
	}

	/**
	 * Adds the functions that are no longer used after version 6.4 if version is at most 6.4.
	 *
	 * @param version
	 *            the version of the associated operator
	 * @return the builder
	 */
	public ExpressionParserBuilder withCompatibility(OperatorVersion version) {
		if (version.isAtMost(OLD_EXPRESSION_PARSER_FUNCTIONS)) {
			compatibleWithOldParser = true;
		}
		return this;
	}

}
