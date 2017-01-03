package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.process.ProcessManagerConfig;

import java.util.List;

/**
 */
public class ProcessPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of();
//        return ImmutableList.of(
//                new SimpleModule(ProcessPioModule.class.getSimpleName())
//                        .registerSubtypes(new NamedType(Connection.class, "connection")
//                        ));
    }

    @Override
    public void configure(Binder binder) {

        JsonConfigProvider.bind(binder, "pio.process", ProcessManagerConfig.class);
        LifecycleModule.register(binder, ProcessManager.class);
//        binder.bind(Connection.class).in(LazySingleton.class);
    }

}
