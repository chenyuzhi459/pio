package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class PopEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(PopEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(PopularTrainingConfig.class, "pop_config"),
                                new NamedType(PopQuery.class, "pop_query"),
                                new NamedType(PopularModelFactory.class, "pop_model")));
    }

    @Override
    public String getEngineName() {
        return "PopEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
