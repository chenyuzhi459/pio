package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.tools.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "chainType", defaultImpl = ProcessRootOperator.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class)
})
public abstract class OperatorChain extends Operator {

    private List<ExecutionUnit> execUnits;
    private List<Connection> connections;

    public OperatorChain(List<Connection> connections, List<ExecutionUnit> execUnits, String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
        super(name, inputPorts, outputPorts);
        this.execUnits = execUnits;
        this.connections = connections;
        if (connections != null && !connections.isEmpty()) {
            initConnections();
        }
        if (execUnits != null && !execUnits.isEmpty()) {
            for (ExecutionUnit unit : execUnits) {
                unit.setEnclosingOperator(this);
            }
        }
    }

//    public void setExecUnits(List<ExecutionUnit> execUnits) {
//        this.execUnits = execUnits;
//        for (ExecutionUnit unit : execUnits) {
//            unit.setEnclosingOperator(this);
//        }
//    }


    private void initConnections() {
        Map<String, Operator> operatorMap = new HashMap<>();
        Map<Pair<String, String>, InputPort> inputMap = new HashMap<>();
        Map<Pair<String, String>, OutputPort> outputMap = new HashMap<>();
        List<Operator> opts;
        Collection<InputPort> inputs;
        Collection<OutputPort> outputs;
        for (ExecutionUnit unit : execUnits) {
            opts = unit.getOperators();
            Preconditions.checkArgument(opts != null && opts.size() > 0, "operators cannot be empty");
            for (Operator opt : opts) {
                Preconditions.checkNotNull(opt.getName(), "Must specify operator name");
                operatorMap.put(opt.getName(), opt);
                inputs = opt.getInputPorts();
                for (InputPort input : inputs) {
                    inputMap.put(new Pair<>(opt.getName(), input.getName()), input);
                }
                outputs = opt.getOutputPorts();
                for (OutputPort output : outputs) {
                    outputMap.put(new Pair<>(opt.getName(), output.getName()), output);
                }
            }
        }
        InputPort input;
        OutputPort output;
        for (Connection conn : connections) {
            input = inputMap.get(new Pair<>(conn.getToOperator(), conn.getToPort()));
            output = outputMap.get(new Pair<>(conn.getFromOperator(), conn.getFromPort()));

            Preconditions.checkNotNull(output, "Cannot find outputPort:[%s-%s]", conn.getFromOperator(), conn.getFromPort());
            output.connectTo(input);
        }
    }

    @JsonProperty("execUnits")
    public List<ExecutionUnit> getExecUnits() {
        return execUnits;
    }

    /**
     * This method returns an arbitrary implementation of {@link InputPorts} for inner sink port
     * initialization. Useful for adding an arbitrary implementation (e.g. changing port creation &
     * (dis)connection behavior, optionally by customized {@link InputPort} instances) by overriding
     * this method.
     *
     * @param portOwner The owner of the ports.
     * @return The {@link InputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
//    protected InputPorts createInnerSinks(PortOwner portOwner) {
//        return new InputPortsImpl(portOwner);
//    }

    /**
     * This method returns an arbitrary implementation of {@link OutputPorts} for inner source port
     * initialization. Useful for adding an arbitrary implementation (e.g. changing port creation &
     * (dis)connection behavior, optionally by customized {@link OutputPort} instances) by
     * overriding this method.
     *
     * @param portOwner The owner of the ports.
     * @return The {@link OutputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
//    protected OutputPorts createInnerSources(PortOwner portOwner) {
//        return new OutputPortsImpl(portOwner);
//    }
    @Override
    public void doWork() {
        for (ExecutionUnit subprocess : execUnits) {
            subprocess.execute();
        }
    }

    public ExecutionUnit getSubprocess(int index) {
        return execUnits.get(index);
    }

    public int getNumberOfSubprocesses() {
        return execUnits.size();
    }

    /**
     * Returns an immutable view of all subprocesses
     */
    public List<ExecutionUnit> getSubprocesses() {
        return execUnits;
    }
}
