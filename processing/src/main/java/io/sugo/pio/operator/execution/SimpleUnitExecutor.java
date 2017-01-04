package io.sugo.pio.operator.execution;

import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Executes an {@link ExecutionUnit} by invoking the operators in their (presorted) ordering.
 * Instances of this class can be shared.
 *
 */
public class SimpleUnitExecutor implements UnitExecutor {
    @Override
    public void execute(ExecutionUnit unit) {
        Iterator<Operator> opIter = unit.getOperatorIterator();

        Operator operator = opIter.hasNext() ? opIter.next() : null;
        while (operator != null) {
            operator.execute();
            operator.freeMemory();

            operator = opIter.hasNext() ? opIter.next() : null;
        }
    }
}
