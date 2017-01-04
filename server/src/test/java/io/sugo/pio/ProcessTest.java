package io.sugo.pio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.operator.io.csv.CSVExampleSource;
import io.sugo.pio.operator.io.csv.CSVWriter;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
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
        ProcessPioModule processPioModule = new ProcessPioModule();
        for(Module m: processPioModule.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void testOperatorChain(){
        List<Connection> conns = new ArrayList<>();
        List<ExecutionUnit> execUnits = new ArrayList<>();
        ProcessRootOperator root = new ProcessRootOperator(conns, execUnits);
    }

    @Test
    public void testProcessRootOperatorDeSerialize(){
        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(new CSVWriter("csv_writer", new InputPortImpl("csv_writer_input")));
        execUnits.add(new ExecutionUnit(operators));
        ProcessRootOperator rootOperator = new ProcessRootOperator(execUnits);
        try {
            String json = jsonMapper.writeValueAsString(rootOperator);
            System.out.println(json);

            ProcessRootOperator root = jsonMapper.readValue(json, ProcessRootOperator.class);
            System.out.println(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
