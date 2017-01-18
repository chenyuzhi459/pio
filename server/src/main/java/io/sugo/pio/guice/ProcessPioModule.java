package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.process.ProcessManagerConfig;
import io.sugo.pio.server.process.csv.FileModifier;
import io.sugo.pio.server.process.csv.FileReader;
import io.sugo.pio.server.process.csv.FileWriter;

import java.util.List;

/**
 */
public class ProcessPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ProcessingPioModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(FileReader.class, "csv_reader"),
                                new NamedType(FileModifier.class, "csv_modifier"),
                                new NamedType(FileWriter.class, "csv_writer")
                        )
        );
    }

    @Override
    public void configure(Binder binder) {
        JsonConfigProvider.bind(binder, "pio.process", ProcessManagerConfig.class);
        LifecycleModule.register(binder, ProcessManager.class);
//        binder.bind(CSVReader.class).in(LazySingleton.class);
    }

}
