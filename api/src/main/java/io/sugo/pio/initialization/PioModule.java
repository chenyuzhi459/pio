package io.sugo.pio.initialization;


import com.fasterxml.jackson.databind.Module;

import java.util.List;

/**
 */
public interface PioModule extends com.google.inject.Module
{
    public List<? extends Module> getJacksonModules();
}
