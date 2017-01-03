package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.Process;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.io.csv.CSVExampleSource;

import java.util.List;

/**
 */
public class ProcessingPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ProcessingPioModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(Process.class, "process")
                        ));
    }

    @Override
    public void configure(Binder binder) {
    }
}
