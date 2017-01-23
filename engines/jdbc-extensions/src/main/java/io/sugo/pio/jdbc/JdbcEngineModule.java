package io.sugo.pio.jdbc;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class JdbcEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(JdbcEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(JdbcBatchEventHose.class, "jdbc")
                                ));
    }

    @Override
    public void configure(Binder binder) {
    }
}
