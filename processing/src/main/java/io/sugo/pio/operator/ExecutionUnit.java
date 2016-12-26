package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.operator.execution.UnitExecutionFactory;
import io.sugo.pio.operator.execution.UnitExecutor;
import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.ports.OutputPorts;
import io.sugo.pio.ports.PortOwner;

import java.util.*;

/**
 */
public class ExecutionUnit {
    private final PortOwner portOwner = new PortOwner() {
        @Override
        public Operator getOperator() {
            return getEnclosingOperator();
        }
    };

    private String name;

    private OperatorChain enclosingOperator;
    private InputPorts innerInputPorts;
    private OutputPorts innerOutputPorts;
    private List<Operator> operators;

    @JsonCreator
    public ExecutionUnit(
            @JsonProperty("operators") List<Operator> operators,
            @JsonProperty("name") String name
    ) {
        this.name = name;
        this.operators = operators;
    }

    /**
     * Returns the operator that contains this process as a subprocess.
     */
    public OperatorChain getEnclosingOperator() {
        return enclosingOperator;
    }

    public void setEnclosingOperator(OperatorChain enclosingOperator) {
        this.enclosingOperator = enclosingOperator;
        innerInputPorts = enclosingOperator.createInnerSinks(portOwner);
        innerOutputPorts = enclosingOperator.createInnerSources(portOwner);
    }

    public InputPorts getInnerSinks() {
        return innerInputPorts;
    }

    public OutputPorts getInnerSources() {
        return innerOutputPorts;
    }

    /**
     * Adds the operator to this execution unit. The operator at this index and all subsequent
     * operators are shifted to the right. The operator is registered automatically.
     */
    public void addOperator(Operator operator, int index) {
        if (operator == null) {
            throw new NullPointerException("operator cannot be null!");
        }
        if (operator instanceof ProcessRootOperator) {
            throw new IllegalArgumentException(
                    "'Process' operator cannot be added. It must always be the top-level operator!");
        }
        operators.add(index, operator);
        registerOperator(operator, true);
    }

    private void registerOperator(Operator operator, boolean registerWithProcess) {
        operator.setEnclosingProcess(this);
        Process process = getEnclosingOperator().getProcess();
        if (process != null && registerWithProcess) {
            operator.registerOperator(process);
        }
    }

    /**
     * Returns an unmodifiable view of the operators contained in this process.
     */
    public List<Operator> getOperators() {
        return Collections.unmodifiableList(new ArrayList<>(operators));
    }

    /**
     * Use this method only in cases where you are sure that you don't want a
     * ConcurrentModificationException to occur when the list of operators is modified.
     */
    public Iterator<Operator> getOperatorIterator() {
        return operators.iterator();
    }

    /**
     * Returns an unmodifiable view of the operators contained in this process.
     */
    public List<Operator> getEnabledOperators() {
        return new EnabledOperatorView(operators);
    }

    /**
     * Executes the inner operators.
     */
    public void execute() {
        UnitExecutor executor = UnitExecutionFactory.getInstance().getExecutor(this);
        executor.execute(this);
    }
}
