package io.sugo.pio.operator.execution;


import io.sugo.pio.operator.ExecutionUnit;

/**
 * Executes an {@link ExecutionUnit}.
 * 
 * @author Simon Fischer
 * */
public interface UnitExecutor {

	public void execute(ExecutionUnit unit);
}
