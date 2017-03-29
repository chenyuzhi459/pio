package io.sugo.pio.engine.bbs;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class BbsEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(BbsEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(BbsEngineFactory.class, "bbs_factory"),
                                new NamedType(BbsQuery.class, "bbs_query"),
                                new NamedType(BbsModelFactory.class, "bbs_model")));
    }

    @Override
    public String getEngineName() {
        return "BbsEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
