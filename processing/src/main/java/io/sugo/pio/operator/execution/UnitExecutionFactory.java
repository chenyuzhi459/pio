package io.sugo.pio.operator.execution;


import io.sugo.pio.operator.ExecutionUnit;

/**
 * Creates instances of {@link UnitExecutor}s for {@link ExecutionUnit}s.
 *
 * @author Simon Fischer
 *
 */
public abstract class UnitExecutionFactory {

	private static UnitExecutionFactory instance = new SimpleUnitExecutionFactory();

	public static UnitExecutionFactory getInstance() {
		return instance;
	}

	public UnitExecutor getExecutor(ExecutionUnit unit) {
		return instance.getExecutor(unit);
	}

}
