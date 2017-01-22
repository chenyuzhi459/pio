package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class DetailEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(DetailEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(DetailEngineFactory.class, "detail_factory"),
                                new NamedType(DetailQuery.class, "detail_query"),
                                new NamedType(DetailModelFactory.class, "detail_model")));
    }

    @Override
    public void configure(Binder binder) {
    }
}
