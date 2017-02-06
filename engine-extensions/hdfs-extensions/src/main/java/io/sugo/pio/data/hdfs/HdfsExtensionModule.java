package io.sugo.pio.data.hdfs;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineExtensionModule;

import java.util.List;

/**
 */
public class HdfsExtensionModule implements EngineExtensionModule{
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(HdfsExtensionModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(HdfsRepository.class, "hdfs")
                        ));
    }

    @Override
    public void configure(Binder binder) {

    }
}
