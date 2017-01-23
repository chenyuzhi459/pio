package io.sugo.pio.engine.fp;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class FpEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(FpEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(FpEngineFactory.class, "fp_factory"),
                                new NamedType(FpQuery.class, "fp_query"),
                                new NamedType(FpModelFactory.class, "fp_model")));
    }

    @Override
    public void configure(Binder binder) {
    }
}