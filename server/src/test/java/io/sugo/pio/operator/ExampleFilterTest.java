package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import org.junit.Test;

public class ExampleFilterTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {
        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void test() throws JsonProcessingException {

        ExampleFilter ef = new ExampleFilter();

        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(ef)
        );
    }

}
