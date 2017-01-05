package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.http.OperatorMetadataHelper;
import io.sugo.pio.jackson.DefaultObjectMapper;
import org.junit.Test;

public class OperatorMetaHelperTest {
    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();

        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
        ProcessPioModule processPioModule = new ProcessPioModule();
        for (Module m : processPioModule.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(
                                OperatorMetadataHelper.getAllOperatorMetadata(jsonMapper).values()));

    }
}
