package io.sugo.pio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
                if (cons.length > 0) {
                    Constructor con = cons[0];
                    Operator opt = null;
                    try {
                        opt = (Operator) con.newInstance(new Object[con.getParameterCount()]);
//                            System.out.println("" + opt.getMetadata());
                        Method method = t.getMethod("getMetadata");
                        Object val = method.invoke(opt, null);
                        System.out.println(val);
                    } catch (Exception e) {
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
    public void testProcessRootOperatorDeSerialize() {
        List<ExecutionUnit> execUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(new CSVWriter("csv_writer", new InputPortImpl("csv_writer_input")));
        execUnits.add(new ExecutionUnit(operators));
        ProcessRootOperator rootOperator = new ProcessRootOperator(null, execUnits);
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
