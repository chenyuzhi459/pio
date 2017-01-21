package io.sugo.pio.engine;

import com.fasterxml.jackson.databind.Module;

import java.util.List;

/**
 */
public interface EngineModule extends com.google.inject.Module
{
    public List<? extends Module> getJacksonModules();
}

