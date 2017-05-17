package io.sugo.pio.engine.userExtension;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class UserExtensionEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(UserExtensionEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(UserExtensionTrainingConfig.class, "userExtension_config"),
                                new NamedType(UserExtensionQuery.class, "userExtension_query"),
                                new NamedType(UserExtensionModelFactory.class, "userExtension_model")));
    }

    @Override
    public String getEngineName() {
        return "UserExtensionEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
