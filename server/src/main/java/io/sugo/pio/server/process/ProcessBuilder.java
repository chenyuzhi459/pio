//package io.sugo.pio.server.process;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.common.base.Preconditions;
//import io.sugo.pio.operator.ExecutionUnit;
//import io.sugo.pio.operator.Operator;
//import io.sugo.pio.operator.ProcessRootOperator;
//import io.sugo.pio.ports.Connection;
//import io.sugo.pio.ports.InputPort;
//import io.sugo.pio.ports.OutputPort;
//import io.sugo.pio.tools.Pair;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ProcessBuilder {
//
//    private List<Connection> connections;
//    private List<ExecutionUnit> execUnits;
//    private String processName;
//
//    @JsonCreator
//    public ProcessBuilder(
//            @JsonProperty("processName") String processName,
//            @JsonProperty("connections") List<Connection> connections,
//            @JsonProperty("excUnits") List<ExecutionUnit> execUnits
//    ) {
//        Preconditions.checkArgument(execUnits != null && execUnits.size() > 0, "excUnits cannot be empty");
//        Preconditions.checkNotNull(processName, "Must specify process name");
//        this.processName = processName;
//        this.connections = connections;
//        this.execUnits = execUnits;
//
//        initConnections();
//    }
//
//    private void initConnections() {
//        Map<String, Operator> operatorMap = new HashMap<>();
//        Map<Pair<String, String>, InputPort> inputMap = new HashMap<>();
//        Map<Pair<String, String>, OutputPort> outputMap = new HashMap<>();
//        List<Operator> opts;
//        Collection<InputPort> inputs;
//        Collection<OutputPort> outputs;
//        for (ExecutionUnit unit : execUnits) {
//            opts = unit.getOperators();
//            Preconditions.checkArgument(opts != null && opts.size() > 0, "operators cannot be empty");
//            for (Operator opt : opts) {
//                Preconditions.checkNotNull(opt.getName(), "Must specify operator name");
//                operatorMap.put(opt.getName(), opt);
//                inputs = opt.getInputPorts();
//                for (InputPort input : inputs) {
//                    inputMap.put(new Pair<>(opt.getName(), input.getName()), input);
//                }
//                outputs = opt.getOutputPorts();
//                for (OutputPort output : outputs) {
//                    outputMap.put(new Pair<>(opt.getName(), output.getName()), output);
//                }
//            }
//        }
//        InputPort input;
//        OutputPort output;
//        for (Connection conn : connections) {
//            input = inputMap.get(new Pair<>(conn.getToOperator(), conn.getToPort()));
//            output = outputMap.get(new Pair<>(conn.getFromOperator(), conn.getFromPort()));
//
//            Preconditions.checkNotNull(output, "Cannot find outputPort:[%s-%s]", conn.getFromOperator(), conn.getFromPort());
//            output.connectTo(input);
//        }
//    }
//
//    @JsonProperty
//    public List<Connection> getConnections() {
//        return connections;
//    }
//
//    @JsonProperty("execUnits")
//    public List<ExecutionUnit> getExecUnits() {
//        return execUnits;
//    }
//
//    @JsonProperty("processName")
//    public String getProcessName() {
//        return processName;
//    }
//
////    public Process getProcess() {
////        ProcessRootOperator root = new ProcessRootOperator(execUnits);
////        Process p = new Process(processName, root);
////        return p;
////    }
//}
