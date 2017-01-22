package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import org.junit.Test;

public class ChangeAttributeRoleTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {
        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void test() throws JsonProcessingException {

        ChangeAttributeRole role = new ChangeAttributeRole();

        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(role)
        );
    }
}
