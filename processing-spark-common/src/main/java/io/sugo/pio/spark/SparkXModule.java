package io.sugo.pio.spark;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 */
public class SparkXModule implements SparkModule {

    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(SparkXModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(TestEngineFactory.class, "test")));
    }
}
