package io.sugo.pio.engine.textSimilar;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.engine.EngineModule;

import java.util.List;

/**
 */
public class TextSimilarEngineModule implements EngineModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(TextSimilarEngineModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(TextSimilarEngineFactory.class, "textSimilar_factory"),
                                new NamedType(TextSimilarQuery.class, "textSimilar_query"),
                                new NamedType(TextSimilarModelFactory.class, "textSimilar_model")));
    }

    @Override
    public String getEngineName() {
        return "TextSimilarEngine";
    }

    @Override
    public void configure(Binder binder) {
    }
}
