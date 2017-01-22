package io.sugo.pio.engine.demo;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.EngineModule;
import io.sugo.pio.engine.als.ALSEngineModule;
import io.sugo.pio.engine.detail.DetailEngineModule;
import io.sugo.pio.engine.fp.FpEngineModule;
import io.sugo.pio.engine.popular.PopEngineModule;
import io.sugo.pio.engine.search.SearchEngineModule;

/**
 */
public class ObjectMapperUtil {
    private static ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
        EngineModule engineModule = new ALSEngineModule();
        for(Module module: engineModule.getJacksonModules()) {
            jsonMapper.registerModule(module);
        }

        engineModule = new DetailEngineModule();
        for(Module module: engineModule.getJacksonModules()) {
            jsonMapper.registerModule(module);
        }

        engineModule = new PopEngineModule();
        for(Module module: engineModule.getJacksonModules()) {
            jsonMapper.registerModule(module);
        }

        engineModule = new FpEngineModule();
        for(Module module: engineModule.getJacksonModules()) {
            jsonMapper.registerModule(module);
        }

        engineModule = new SearchEngineModule();
        for(Module module: engineModule.getJacksonModules()) {
            jsonMapper.registerModule(module);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return jsonMapper;
    }

}
