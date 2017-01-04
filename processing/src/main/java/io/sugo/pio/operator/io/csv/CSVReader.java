package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Arrays;

/**
 * Created by root on 17-1-2.
 */
public class CSVReader extends Operator {
    public static final String TYPE = "csv_reader";

    @JsonCreator
    public CSVReader(
            @JsonProperty("name") String name,
            @JsonProperty("outputPort") OutputPort outputPort
    ) {
        super(name, null, Arrays.asList(outputPort));
    }

    public void doWork() {
        System.out.println("CSVReader do work");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new OperatorException(TYPE + " error", e);
        }
        System.out.println("CSVReader do work finished after 10 seconds");
    }
}
