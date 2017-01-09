package io.sugo.pio.operator.io.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.OutputPort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVReaderTest extends Operator {
    private final OutputPort outputPort;
    private final int readTime;
    private final String tableName;
    private final double score;
    private final List<String> data;
    private final Map<Integer, String> map;
    private final Person person;

    @JsonCreator
    public CSVReaderTest(
            @JsonProperty("name") String name,
            @JsonProperty("readTime") int readTime,
            @JsonProperty("tableName") String tableName,
            @JsonProperty("score") double score,
            @JsonProperty("data") List<String> data,
            @JsonProperty("map") Map<Integer, String> map,
            @JsonProperty("person") Person person,
            @JsonProperty("outputPort") OutputPort outputPort
    ) {
        super(name, null, Arrays.asList(outputPort));
        this.readTime = readTime;
        this.tableName = tableName;
        this.score = score;
        this.data = data;
        this.map = map;
        this.person = person;
        this.outputPort = outputPort;
    }

    public void doWork() {
        System.out.println("CSVReaderTest do work");
        try {
            Thread.sleep(readTime * 1000);
            outputPort.deliver(null);
        } catch (InterruptedException e) {
            throw new OperatorException(CSVReaderMeta.TYPE + " error", e);
        }
        System.out.println("CSVReaderTest do work finished after " + readTime + " seconds");
    }
}
