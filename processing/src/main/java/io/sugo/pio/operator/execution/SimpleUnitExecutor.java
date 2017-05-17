package io.sugo.pio.operator.execution;

import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.Status;

import java.util.Iterator;

/**
 * Executes an {@link ExecutionUnit} by invoking the operators in their (presorted) ordering.
 * Instances of this class can be shared.
 */
public class SimpleUnitExecutor implements UnitExecutor {

    private static final Logger log = new Logger(SimpleUnitExecutor.class);

    @Override
    public void execute(ExecutionUnit unit) {
        String runStartOperatorId = unit.getRunStartOperatorId();
        String runEndOperatorId = unit.getRunEndOperatorId();
        boolean startRun = runStartOperatorId == null;

        Iterator<Operator> opIter = unit.getOperatorIterator();
        while (opIter.hasNext()) {
            Operator operator = opIter.next();
            String name = operator.getName();

            if (runStartOperatorId != null && name.equals(runStartOperatorId)) {
                startRun = true;
            }

            if (startRun) {
                log.info("Begin to execute operator named: <<<%s>>> ...", name);

                operator.execute();
                operator.freeMemory();

                log.info("Execute operator named: <<<%s>>> successfully.", name);
            } else {
                log.info("Ignore execution of operator: <<<%s>>>.", name);
            }

            if (runEndOperatorId != null && name.equals(runEndOperatorId)) {
                log.info("Process execution ended at operator: <<<%s>>>, and ignore the remaining operators.", name);
                break;
            }
        }
    }
}
