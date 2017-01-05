package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.ports.InputPort;

import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 17-1-2.
 */
public class CSVWriter extends Operator {
    private final InputPort inputPort;
    private final int writeTime;

    @JsonCreator
    public CSVWriter(
            @JsonProperty("name") String name,
            @JsonProperty("writeTime") int writeTime,
            @JsonProperty("inputPort") InputPort inputPort
    ) {
        super(name, Arrays.asList(inputPort), null);
        this.inputPort = inputPort;
        this.writeTime = writeTime;
    }

    @JsonProperty
    public int getWriteTime() {
        return writeTime;
    }

    @JsonProperty
    public InputPort getInputPort() {
        return inputPort;
    }

    public void doWork() {
        System.out.println("CSVWriter do work");
        try {
            inputPort.receive(null);
            Thread.sleep(writeTime * 1000);
        } catch (InterruptedException e) {
            throw new OperatorException(CSVWriterMeta.TYPE + " error", e);
        }
        System.out.println("CSVWriter do work finished after " + writeTime + " seconds");
    }
}
