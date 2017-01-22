package io.sugo.pio.engine.userHistory;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class UserHistoryEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(UserHistoryEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(UserHistoryEngineFactory.class, "UserHistory_factory"),
                                new NamedType(UserHistoryQuery.class, "UserHistory_query"),
                                new NamedType(UserHistoryModelFactory.class, "UserHistory_model")));
    }

    @Override
    public void configure(Binder binder) {
    }
}
