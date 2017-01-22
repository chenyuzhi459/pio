package io.sugo.pio.engine.search;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class SearchEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(SearchEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(SearchEngineFactory.class, "Search_factory"),
                                new NamedType(SearchQuery.class, "Search_query"),
                                new NamedType(SearchModelFactory.class, "Search_model")));
    }

    @Override
    public void configure(Binder binder) {
    }
}
