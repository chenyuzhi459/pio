package io.sugo.pio.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.io.csv.CSVModifier;
import io.sugo.pio.operator.io.csv.CSVReader;
import io.sugo.pio.operator.io.csv.CSVWriter;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-12-27.
 */
public class ProcessTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {
        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void testOperatorProcess() throws JsonProcessingException {
        List<Connection> conns = new ArrayList<>();

        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        ExecutionUnit unit = new ExecutionUnit(operators);
        execUnits.add(unit);

        OutputPort readerOut = new OutputPortImpl("readerOut");
        CSVReader reader = new CSVReader("reader", 5, readerOut);
        operators.add(reader);

        InputPort modifierIn = new InputPortImpl("modifierIn");
        OutputPort modifierOut = new OutputPortImpl("modifierOut");
        CSVModifier modifier = new CSVModifier("modifier", 4, modifierIn, modifierOut);
        operators.add(modifier);

        InputPort writerIn = new InputPortImpl("writerIn");
        CSVWriter writer = new CSVWriter("writer", 6, writerIn);
        operators.add(writer);

        conns.add(new Connection("reader", "readerOut", "modifier", "modifierIn"));
        conns.add(new Connection("modifier", "modifierOut", "writer", "writerIn"));

        ProcessRootOperator root = new ProcessRootOperator(conns, execUnits);
        OperatorProcess process = new OperatorProcess("testProcess", root);

        String json = jsonMapper.writeValueAsString(process);
        System.out.println(json);
        process.run();
        json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(process);
        System.out.println(json);
    }
}
