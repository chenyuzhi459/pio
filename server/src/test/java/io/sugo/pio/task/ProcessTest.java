package io.sugo.pio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.operator.io.csv.CSVExampleSource;
import io.sugo.pio.server.process.Connection;
import io.sugo.pio.server.process.ProcessBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-12-27.
 */
public class ProcessTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {
        ProcessingPioModule module = new ProcessingPioModule();
        for(Module m: module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }
    @Test
    public void testProcess(){
        List<ExecutionUnit> units = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        Operator csvReader = new CSVExampleSource("/work/win7/druid.csv", "csv_reader");
        csvReader.getInputPorts();
        csvReader.getOutputPorts();
        operators.add(csvReader);
        units.add(new ExecutionUnit(operators));
        ProcessRootOperator rootOperator = new ProcessRootOperator(units);
        Process process = new Process("testProcess", rootOperator);

        String json = null;
        Process deProcess = null;
        try {
            json = jsonMapper.writeValueAsString(process);
            System.out.println(json);

            deProcess = jsonMapper.readValue(json, Process.class);

            json = jsonMapper.writeValueAsString(deProcess);
            System.out.println(json);

            deProcess.run();

            json = jsonMapper.writeValueAsString(deProcess);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Assert.assertEquals(process, deProcess);
    }

    @Test
    public void testProcessBuilder(){
        List<Connection> connections = new ArrayList<>();
        Connection conn = new Connection();
        conn.setFromOpt("fromOpt");
        conn.setFromPort("fromPort");
        conn.setToOpt("toOpt");
        conn.setToPort("toPort");
//        connections.add(conn);

        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(new CSVExampleSource("/work/win7/druid.csv", "csv_reader"));
        ExecutionUnit execUnit = new ExecutionUnit(operators);
        execUnits.add(execUnit);
        ProcessBuilder builder = new ProcessBuilder("process1", connections, execUnits);

        String json = null;
        try {
            json = jsonMapper.writeValueAsString(builder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    @Test
    public void testCsvReaderDeSerialize(){
        CSVExampleSource csvReader = new CSVExampleSource("/work/win7/druid.csv", "csv_reader");
        try {
            csvReader.setStatus(Status.RUNNING);
            String json = jsonMapper.writeValueAsString(csvReader);
            System.out.println(json);

            CSVExampleSource newCsvReader = jsonMapper.readValue(json, CSVExampleSource.class);

            json = jsonMapper.writeValueAsString(newCsvReader);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
