package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.InputPort;

import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 17-1-2.
 */
public class CSVWriter extends Operator {
    public static final String TYPE = "csv_writer";

    @JsonCreator
    public CSVWriter(
            @JsonProperty("name") String name,
            @JsonProperty("inputPort") InputPort inputPort
    ) {
        super(name, Arrays.asList(inputPort), null);
    }

    public void doWork() {
        System.out.println("CSVWriter do work");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("CSVWriter do work finished after 20 seconds");
    }
}
