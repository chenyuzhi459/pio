package io.sugo.pio.engine.als;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class ALSEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ALSEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(ALSEngineFactory.class, "als_factory"),
                                new NamedType(ALSQuery.class, "als_query"),
                                new NamedType(ALSModelFactory.class, "als_model")));
    }

    @Override
    public String getEngineName() {
        return "ALSEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
