package io.sugo.pio.operator.execution;

import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;

import java.util.Enumeration;

/**
 * Executes an {@link ExecutionUnit} by invoking the operators in their (presorted) ordering.
 * Instances of this class can be shared.
 *
 */
public class SimpleUnitExecutor implements UnitExecutor {
    @Override
    public void execute(ExecutionUnit unit) {
        Enumeration<Operator> opEnum = unit.getOperatorEnumeration();

        Operator operator = opEnum.hasMoreElements() ? opEnum.nextElement() : null;
        while (operator != null) {
            operator.execute();
            operator.freeMemory();

            operator = opEnum.hasMoreElements() ? opEnum.nextElement() : null;
        }
    }
}