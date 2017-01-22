package io.sugo.pio.tools.expression.internal;


import io.sugo.pio.tools.expression.Constant;
import io.sugo.pio.tools.expression.ExpressionParserModule;
import io.sugo.pio.tools.expression.Function;
import io.sugo.pio.tools.expression.internal.function.basic.*;
import io.sugo.pio.tools.expression.internal.function.comparison.*;
import io.sugo.pio.tools.expression.internal.function.logical.And;
import io.sugo.pio.tools.expression.internal.function.logical.Not;
import io.sugo.pio.tools.expression.internal.function.logical.Or;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton that holds the standard operations (+,-,*,...).
 *
 * @author Gisa Schaefer
 *
 */
public enum StandardOperations implements ExpressionParserModule {

	INSTANCE;

	private List<Function> standardOperations = new LinkedList<>();

	private StandardOperations() {

		// logical operations
		standardOperations.add(new Not());
		standardOperations.add(new And());
		standardOperations.add(new Or());

		// comparison operations
		standardOperations.add(new Equals());
		standardOperations.add(new NotEquals());
		standardOperations.add(new LessThan());
		standardOperations.add(new GreaterThan());
		standardOperations.add(new LessEqualThan());
		standardOperations.add(new GreaterEqualThan());

		// basic operations
		standardOperations.add(new Plus());
		standardOperations.add(new Minus());
		standardOperations.add(new Multiply());
		standardOperations.add(new Divide());
		standardOperations.add(new Power());
		standardOperations.add(new Modulus());
	}

	@Override
	public String getKey() {
		return "";
	}

	@Override
	public List<Constant> getConstants() {
		return null;
	}

	@Override
	public List<Function> getFunctions() {
		return standardOperations;
	}

}
