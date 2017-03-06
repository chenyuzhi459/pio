package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class ArtiClusterEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ArtiClusterEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(ArtiClusterEngineFactory.class, "artiCluster_factory"),
                                new NamedType(ArtiClusterQuery.class, "artiCluster_query"),
                                new NamedType(ArtiClusterModelFactory.class, "artiCluster_model")));
    }

    @Override
    public String getEngineName() {
        return "ArtiClusterEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
