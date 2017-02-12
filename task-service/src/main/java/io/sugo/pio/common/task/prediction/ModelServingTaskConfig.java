package io.sugo.pio.common.task.prediction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.sugo.pio.guice.GuiceInjectors;
import io.sugo.pio.initialization.Initialization;

/**
 */
public class ModelServingTaskConfig {
    private static final Injector injector;

    public static final ObjectMapper JSON_MAPPER;

    static {
        injector = Initialization.makeInjectorWithModules(
                GuiceInjectors.makeStartupInjector(),
                ImmutableList.<Module>of(
                )
        );
        JSON_MAPPER = injector.getInstance(ObjectMapper.class);
    }

}
