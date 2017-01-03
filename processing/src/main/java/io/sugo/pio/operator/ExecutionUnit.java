package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.operator.execution.UnitExecutionFactory;
import io.sugo.pio.operator.execution.UnitExecutor;
import io.sugo.pio.ports.*;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;

/**
 */
public class ExecutionUnit implements Serializable {

    private OperatorChain enclosingOperator;
    private List<Operator> operators;

    @JsonCreator
    public ExecutionUnit(
            @JsonProperty("operators") List<Operator> operators
    ) {
        this.operators = operators;
    }

    public void setEnclosingOperator(OperatorChain enclosingOperator) {
        this.enclosingOperator = enclosingOperator;
    }

    public List<InputPort> getAllInputPorts(){
        List<InputPort> inputPorts = new ArrayList<>();
        for(Operator opt : operators){
            inputPorts.addAll(opt.getInputPorts());
        }
        return inputPorts;
    }


    /**
     * Returns an unmodifiable view of the operators contained in this process.
     */
    @JsonProperty("operators")
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
     * Returns the operator that contains this process as a subprocess.
     */
    public OperatorChain getEnclosingOperator() {
        return enclosingOperator;
    }

    /**
     * Executes the inner operators.
     */
    public void execute() {
        UnitExecutor executor = UnitExecutionFactory.getInstance().getExecutor(this);
        executor.execute(this);
    }
}
