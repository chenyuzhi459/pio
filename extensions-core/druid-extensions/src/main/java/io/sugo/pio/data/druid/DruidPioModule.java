package io.sugo.pio.data.druid;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;

import java.util.List;

/**
 */
public class DruidPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(DruidPioModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(DruidBatchEventHose.class, "druid")));
    }

    @Override
    public void configure(Binder binder) {

    }
}
