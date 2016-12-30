package io.sugo.pio.server.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import io.sugo.pio.Process;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessBuilder {

    private List<Connection> connections;
    private List<ExecutionUnit> excUnits;
    private String processName;

    @JsonCreator
    public ProcessBuilder(
            @JsonProperty("processName") String processName,
            @JsonProperty("connections") List<Connection> connections,
            @JsonProperty("excUnits") List<ExecutionUnit> excUnits
    ) {
        Preconditions.checkArgument(excUnits != null && excUnits.size() > 0, "excUnits cannot be empty");
        Preconditions.checkNotNull(processName, "Must specify process name");
        this.processName = processName;
        this.connections = connections;
        this.excUnits = excUnits;

        initConnections();
    }

    private void initConnections() {
        Map<String, Operator> operatorMap = new HashMap<>();
        Map<String, InputPort> inputMap = new HashMap<>();
        Map<String, OutputPort> outputMap = new HashMap<>();
        List<Operator> opts;
        List<InputPort> inputs;
        List<OutputPort> outputs;
        for (ExecutionUnit unit : excUnits) {
            opts = unit.getOperators();
            Preconditions.checkArgument(opts != null && opts.size() > 0, "operators cannot be empty");
            for (Operator opt : opts) {
                Preconditions.checkNotNull(opt.getName(), "Must specify operator name");
                operatorMap.put(opt.getName(), opt);
                inputs = opt.getInputPorts().getAllPorts();
                for (InputPort input : inputs) {
                    inputMap.put(input.getName(), input);
                }
                outputs = opt.getOutputPorts().getAllPorts();
                for (OutputPort output : outputs) {
                    outputMap.put(output.getName(), output);
                }
            }
        }
        InputPort input;
        OutputPort output;
        for (Connection conn : connections) {
            input = inputMap.get(conn.getFromPort());
            output = outputMap.get(conn.getToPort());
            Preconditions.checkNotNull(output, "Cannot find outputPort:[%s]", conn.getToPort());
            output.connectTo(input);
        }
    }

    @JsonProperty
    public List<Connection> getConnections() {
        return connections;
    }

    @JsonProperty("excUnits")
    public List<ExecutionUnit> getExcUnits() {
        return excUnits;
    }

    @JsonProperty("processName")
    public String getProcessName() {
        return processName;
    }

    public Process getProcess() {
        ProcessRootOperator root = new ProcessRootOperator(excUnits);
        Process p = new Process(processName, root);
        return p;
    }
}
