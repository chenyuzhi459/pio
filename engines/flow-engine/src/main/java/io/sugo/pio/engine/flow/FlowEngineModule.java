package io.sugo.pio.engine.flow;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class FlowEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(FlowEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(FlowTrainingConfig.class, "flow_config"),
                                new NamedType(FlowQuery.class, "flow_query"),
                                new NamedType(FlowModelFactory.class, "flow_model")));
    }

    @Override
    public String getEngineName() {
        return "FlowEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
