package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.OutputPort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 17-1-2.
 */
public class CSVReader extends Operator {
    private final OutputPort outputPort;
    private final int readTime;

    @JsonCreator
    public CSVReader(
            @JsonProperty("name") String name,
            @JsonProperty("readTime") int readTime,
            @JsonProperty("outputPort") OutputPort outputPort
    ) {
        super(name, null, Arrays.asList(outputPort));
        this.readTime = readTime;
        this.outputPort = outputPort;
    }

    @JsonProperty
    public int getReadTime() {
        return readTime;
    }

    @JsonProperty
    public OutputPort getOutputPort() {
        return outputPort;
    }

    public void doWork() {
        System.out.println("CSVReader do work");
        try {
            Thread.sleep(readTime * 1000);
            outputPort.deliver(null);
        } catch (InterruptedException e) {
            throw new OperatorException(CSVReaderMeta.TYPE + " error", e);
        }
        System.out.println("CSVReader do work finished after " + readTime + " seconds");
    }
}
