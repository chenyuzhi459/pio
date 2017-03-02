package io.sugo.pio.operator.execution;

import com.metamx.common.logger.Logger;
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

    private static final Logger log = new Logger(SimpleUnitExecutor.class);

    @Override
    public void execute(ExecutionUnit unit) {
        Iterator<Operator> opIter = unit.getOperatorIterator();

        Operator operator = opIter.hasNext() ? opIter.next() : null;
        while (operator != null) {
            String name = operator.getName();
            log.info("Begin to execute operator named: %s ...", name);

            operator.execute();
            operator.freeMemory();

            log.info("Execute operator named: %s successfully.", name);

            operator = opIter.hasNext() ? opIter.next() : null;
        }
    }
}
