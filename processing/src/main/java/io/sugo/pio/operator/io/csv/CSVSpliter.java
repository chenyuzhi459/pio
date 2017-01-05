package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Arrays;
import java.util.List;

public class CSVSpliter extends Operator {
    private final InputPort inputPort;
    private final OutputPort outputPort;
    private final List<InputPort> inputPorts;
    private final List<OutputPort> outputPorts;
    private final int modifyTime;

    @JsonCreator
    public CSVSpliter(
            @JsonProperty("name") String name,
            @JsonProperty("modifyTime") int modifyTime,
            @JsonProperty("inputPort") InputPort inputPort,
            @JsonProperty("outputPort") OutputPort outputPort,
            @JsonProperty("inputPorts") List<InputPort> inputPorts,
            @JsonProperty("outputPorts") List<OutputPort> outputPorts
    ) {
        super(name, inputPorts, outputPorts);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.inputPorts = inputPorts;
        this.outputPorts = outputPorts;
        this.modifyTime = modifyTime;
    }

    @JsonProperty
    public int getModifyTime() {
        return modifyTime;
    }

    @JsonProperty
    @Override
    public List<InputPort> getInputPorts() {
        return inputPorts;
    }

    @JsonProperty
    @Override
    public List<OutputPort> getOutputPorts() {
        return outputPorts;
    }

    public void doWork() {
        System.out.println("CSVSpliter do work");
        try {
            inputPorts.get(0).receive(null);
            Thread.sleep(modifyTime * 1000);
            outputPorts.get(0).deliver(null);
        } catch (InterruptedException e) {
            throw new OperatorException(CSVModifierMeta.TYPE + " error", e);
        }
        System.out.println("CSVSpliter do work finished after " + modifyTime + " seconds");
    }
}
