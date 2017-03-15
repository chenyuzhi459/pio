package io.sugo.pio.scripting.python;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;

import java.util.List;

/**
 */
public class PythonScriptModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(new SimpleModule(PythonScriptModule.class.getSimpleName())
               );
    }

    @Override
    public void configure(Binder binder) {
    }
}