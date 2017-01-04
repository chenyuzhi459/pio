package io.sugo.pio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.operator.io.csv.CSVExampleSource;
import io.sugo.pio.operator.io.csv.CSVWriter;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
import io.sugo.pio.server.process.Connection;
import io.sugo.pio.server.process.ProcessBuilder;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

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
        ProcessPioModule processPioModule = new ProcessPioModule();
        for (Module m : processPioModule.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void getSubtypeClasses() {
        Class c = Operator.class;
        MapperConfig config = jsonMapper.getDeserializationConfig();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(c, ai, config);
        List<NamedType> subtypes = ai.findSubtypes(ac);

        if (subtypes != null) {
            for (NamedType subtype : subtypes) {
                System.out.println("------");
                System.out.println(subtype.getName());
                System.out.println(subtype.getType());
                Class<?> t = subtype.getType();
                    Constructor[] cons = t.getDeclaredConstructors();
                    if(cons.length > 0){
                        Constructor con = cons[0];
                        Operator opt = null;
                        try {
                            opt = (Operator) con.newInstance(new Object[con.getParameterCount()]);
                            System.out.println("" + opt.getMetadata());
                            Method method = t.getMethod("getMetadata");
                            Object val = method.invoke(opt, null);
                            System.out.println(val);
                        }catch (Exception e){
//                            System.out.println("" + opt.getMetadata());
                            Method method = null;
                            try {
                                method = t.getMethod("getMetadata");
                                Object val = method.invoke(opt, null);
                                System.out.println(val);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    for (int i = 0; i < cons.length; i++) {
                        Constructor con = cons[i];
                    }

//                System.out.println(subtype.getType());
            }
        }
    }

    @Test
    public void testProcess() {
        List<ExecutionUnit> units = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();

        Operator csvReader = new CSVExampleSource("druid.csv", "csv_reader", new OutputPortImpl("csv_reader_output"));
        csvReader.getInputPorts();
        csvReader.getOutputPorts();
        operators.add(csvReader);
        CSVWriter csvWriter = new CSVWriter("csv_writer", new InputPortImpl("csv_writer_input"));
        operators.add(csvWriter);

        units.add(new ExecutionUnit(operators));
        ProcessRootOperator rootOperator = new ProcessRootOperator(units);
        Process process = new Process("testProcess", rootOperator);

        process.run();

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
    public void testProcessBuilder() {
        List<Connection> connections = new ArrayList<>();
        Connection conn = new Connection("csv_reader", "csv_reader_output", "csv_writer", "csv_writer_input");
//        conn.setFromOpt("csv_reader");
//        conn.setFromPort("csv_reader_output");
//        conn.setToOpt("csv_writer");
//        conn.setToPort("csv_writer_input");

        connections.add(conn);

        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(new CSVExampleSource("druid.csv", "csv_reader", new OutputPortImpl("csv_reader_output")));
        operators.add(new CSVWriter("csv_writer", new InputPortImpl("csv_writer_input")));
        ExecutionUnit execUnit = new ExecutionUnit(operators);
        execUnits.add(execUnit);
        ProcessBuilder builder = new ProcessBuilder("process1", connections, execUnits);

        String json = null;
        try {
            json = jsonMapper.writeValueAsString(builder);
            System.out.println(json);

//            ProcessBuilder newBuilder = jsonMapper.readValue(json, ProcessBuilder.class);
//            json = jsonMapper.writeValueAsString(newBuilder);
//            System.out.println(json);

            Process process = builder.getProcess();
            json = jsonMapper.writeValueAsString(process);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCsvReaderDeSerialize() {
        CSVExampleSource csvReader = new CSVExampleSource("/work/win7/druid.csv", "csv_reader", new OutputPortImpl("csv_reader_output"));
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

    @Test
    public void testProcessRootOperatorDeSerialize() {
        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(new CSVWriter("csv_writer", new InputPortImpl("csv_writer_input")));
        execUnits.add(new ExecutionUnit(operators));
        ProcessRootOperator rootOperator = new ProcessRootOperator(execUnits);
        try {
            String json = jsonMapper.writeValueAsString(rootOperator);
            System.out.println(json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
