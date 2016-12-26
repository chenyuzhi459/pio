package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.operator.execution.UnitExecutionFactory;
import io.sugo.pio.operator.execution.UnitExecutor;
import io.sugo.pio.ports.*;

import java.util.*;
import java.util.logging.Level;

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

//    public ExecutionUnit(OperatorChain enclosingOperator, String name) {
//        this.name = name;
//        this.enclosingOperator = enclosingOperator;
//
//        innerInputPorts = enclosingOperator.createInnerSinks(portOwner);
//        innerOutputPorts = enclosingOperator.createInnerSources(portOwner);
//    }

    @JsonCreator
    public ExecutionUnit(
            @JsonProperty("operators") List<Operator> operators,
            @JsonProperty("name") String name
    ) {
        this.name = name;
        this.operators = operators;
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
     * Same as {@link #addOperator(Operator, boolean)}.
     */
    public int addOperator(Operator operator) {
        return addOperator(operator, true);
    }

    /**
     * Adds the operator to this execution unit.
     *
     * @param registerWithProcess Typically true. If false, the operator will not be registered with its parent
     *                            process.
     * @return the new index of the operator.
     */
    public int addOperator(Operator operator, boolean registerWithProcess) {
        if (operator == null) {
            throw new NullPointerException("operator cannot be null!");
        }
        if (operator instanceof ProcessRootOperator) {
            throw new IllegalArgumentException(
                    "'Process' operator cannot be added. It must always be the top-level operator!");
        }
        operators.add(operator);
        registerOperator(operator, registerWithProcess);
        return operators.size() - 1;
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
     * Returns the operator that contains this process as a subprocess.
     */
    public OperatorChain getEnclosingOperator() {
        return enclosingOperator;
    }

    private void unwire(boolean recursive) {
        getInnerSources().disconnectAll();
        for (Operator op : getOperators()) {
            unwire(op, recursive);
        }
    }

    private void unwire(Operator op, boolean recursive) {
        op.getOutputPorts().disconnectAll();
        if (recursive) {
            if (op instanceof OperatorChain) {
                for (ExecutionUnit subprocess : ((OperatorChain) op).getSubprocesses()) {
                    subprocess.unwire(recursive);
                }
            }
        }
    }

    private void autoWire(InputPorts inputPorts, LinkedList<OutputPort> readyOutputs) {
        boolean success = false;
        do {
            Set<InputPort> complete = new HashSet<InputPort>();
            for (InputPort in : inputPorts.getAllPorts()) {
                success = false;
                if (!in.isConnected() && !complete.contains(in)
                        && in.getPorts().getOwner().getOperator().shouldAutoConnect(in)) {
                    Iterator<OutputPort> outIterator;
                    outIterator = readyOutputs.descendingIterator();
                    while (outIterator.hasNext()) {
                        OutputPort outCandidate = outIterator.next();
                        // TODO: Remove shouldAutoConnect() in later versions
                        Operator owner = outCandidate.getPorts().getOwner().getOperator();
                        if (owner.shouldAutoConnect(outCandidate)) {
                            if (outCandidate.getMetaData() != null) {
                                if (in.isInputCompatible(outCandidate.getMetaData())) {
                                    readyOutputs.remove(outCandidate);
                                    outCandidate.connectTo(in);
                                    // we cannot continue with the remaining input ports
                                    // since connecting may have triggered the creation of new input
                                    // ports
                                    // which would result in undefined behavior and a
                                    // ConcurrentModificationException
                                    success = true;
                                    break;
                                }
                            }
                        }
                    }
                    // no port found.
                    complete.add(in);
                    if (success) {
                        break;
                    }
                }
            }
        } while (success);
    }

    /**
     * Transforms the meta data of the enclosing operator. Required in {@link #autoWire(List, LinkedList, boolean, boolean)} ()} after
     * each Operator that has been wired.
     */
    private void transformMDNeighbourhood() {
        getEnclosingOperator().transformMetaData();
    }


    /**
     * Connects the ports automatically in a first-fit approach. Operators are connected in their
     * ordering within the {@link #operators} list. Every input of every operator is connected to
     * the first compatible output of an operator "left" of this operator. This corresponds to the
     * way, IOObjects were consumed in the pre-5.0 version. Disabled operators are skipped.
     * <p>
     * <br/>
     *
     * @param keepConnections if true, don't unwire old connections before rewiring.
     */
    public void autoWire(boolean keepConnections, boolean recursive) {
        if (!keepConnections) {
            unwire(recursive);
        }
        // store all outputs. Scan them to find matching inputs.
        LinkedList<OutputPort> readyOutputs = new LinkedList<OutputPort>();
        addReadyOutputs(readyOutputs, getInnerSources());
        List<Operator> enabled = new LinkedList<Operator>();
        for (Operator op : getOperators()) {
            if (op.isEnabled()) {
                enabled.add(op);
            }
        }
        autoWire(enabled, readyOutputs, recursive, true);
    }

    /**
     * @param wireNew If true, OutputPorts of operators will be added to readyOutputs once they are
     *                wired.
     */
    private void autoWire(List<Operator> operators, LinkedList<OutputPort> readyOutputs,
                          boolean recursive, boolean wireNew) {
        transformMDNeighbourhood();

        for (Operator op : operators) {
            readyOutputs = op.preAutoWire(readyOutputs);
            autoWire(op.getInputPorts(), readyOutputs);
            transformMDNeighbourhood();
            if (recursive) {
                if (op instanceof OperatorChain) {
                    for (ExecutionUnit subprocess : ((OperatorChain) op).getSubprocesses()) {
                        // we have already removed all connections, so keepConnections=true in
                        // recursive call
                        subprocess.autoWire(true, recursive);
                    }
                }
            }
            if (wireNew) {
                addReadyOutputs(readyOutputs, op.getOutputPorts());
            }
        }
        autoWire(getInnerSinks(), readyOutputs);
        transformMDNeighbourhood();
    }


    private void addReadyOutputs(LinkedList<OutputPort> readyOutputs, OutputPorts ports) {
        // add the parameters in a stack-like fashion like in pre-5.0
        Iterator<OutputPort> i = new LinkedList<OutputPort>(ports.getAllPorts()).descendingIterator();
        while (i.hasNext()) {
            OutputPort port = i.next();
            if (!port.isConnected() && port.shouldAutoConnect()) {
                readyOutputs.addLast(port);
            }
        }
    }

    /**
     * Executes the inner operators.
     */
    public void execute() {
        UnitExecutor executor = UnitExecutionFactory.getInstance().getExecutor(this);
        executor.execute(this);
    }
}
