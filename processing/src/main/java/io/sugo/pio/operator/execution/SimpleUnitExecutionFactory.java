package io.sugo.pio.operator.execution;


import io.sugo.pio.operator.ExecutionUnit;

/**
 * Returns a shared instance of a {@link SimpleUnitExecutor}.
 *
 * @author Simon Fischer
 *
 */
public class SimpleUnitExecutionFactory extends UnitExecutionFactory {

	private final SimpleUnitExecutor executor = new SimpleUnitExecutor();

	@Override
	public UnitExecutor getExecutor(ExecutionUnit unit) {
		return executor;
	}

}
