package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Arrays;

public class CSVModifier extends Operator {
    private final InputPort inputPort;
    private final OutputPort outputPort;
    private final int modifyTime;

    @JsonCreator
    public CSVModifier(
            @JsonProperty("name") String name,
            @JsonProperty("modifyTime") int modifyTime,
            @JsonProperty("inputPort") InputPort inputPort,
            @JsonProperty("outputPort") OutputPort outputPort
    ) {
        super(name, Arrays.asList(inputPort), Arrays.asList(outputPort));
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.modifyTime = modifyTime;
    }

    @JsonProperty
    public int getModifyTime() {
        return modifyTime;
    }

    @JsonProperty
    public InputPort getInputPort() {
        return inputPort;
    }

    @JsonProperty
    public OutputPort getOutputPort() {
        return outputPort;
    }

    public void doWork() {
        System.out.println("CSVModifier do work");
        try {
            inputPort.receive(null);
            Thread.sleep(modifyTime * 1000);
            outputPort.deliver(null);
        } catch (InterruptedException e) {
            throw new OperatorException(CSVModifierMeta.TYPE + " error", e);
        }
        System.out.println("CSVModifier do work finished after " + modifyTime + " seconds");
    }
}
