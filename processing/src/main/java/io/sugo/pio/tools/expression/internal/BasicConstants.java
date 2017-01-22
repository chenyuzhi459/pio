package io.sugo.pio.tools.expression.internal;


import io.sugo.pio.tools.expression.Constant;
import io.sugo.pio.tools.expression.ExpressionParserModule;
import io.sugo.pio.tools.expression.Function;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton that stores the basic constants that can be used by the {@link ExpressionParser}.
 *
 * @author Gisa Schaefer
 *
 */
public enum BasicConstants implements ExpressionParserModule {

	INSTANCE;

	private List<Constant> constants;

	private BasicConstants() {
		constants = new LinkedList<>();
		constants.add(new SimpleConstant("true", true, null, true));
		constants.add(new SimpleConstant("false", false, null, true));
		constants.add(new SimpleConstant("TRUE", true));
		constants.add(new SimpleConstant("FALSE", false));
		constants.add(new SimpleConstant("e", Math.E));
		constants.add(new SimpleConstant("pi", Math.PI, null, true));
		constants.add(new SimpleConstant("PI", Math.PI));
		constants.add(new SimpleConstant("INFINITY", Double.POSITIVE_INFINITY));
		constants.add(new SimpleConstant("MISSING_NOMINAL", (String) null));
		constants.add(new SimpleConstant("MISSING_DATE", (Date) null));
		constants.add(new SimpleConstant("MISSING_NUMERIC", Double.NaN));
		constants.add(new SimpleConstant("MISSING", (String) null, null, true));
		constants.add(new SimpleConstant("NaN", Double.NaN, null, true));
		constants.add(new SimpleConstant("NAN", Double.NaN, null, true));
	}

	@Override
	public String getKey() {
		return "core.basic";
	}

	@Override
	public List<Constant> getConstants() {
		return constants;
	}

	@Override
	public List<Function> getFunctions() {
		return null;
	}

}
