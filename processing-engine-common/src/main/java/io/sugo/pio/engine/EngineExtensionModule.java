package io.sugo.pio.engine;

import com.fasterxml.jackson.databind.Module;

import java.util.List;

/**
 */
public interface EngineExtensionModule extends com.google.inject.Module
{
    List<? extends Module> getJacksonModules();
}