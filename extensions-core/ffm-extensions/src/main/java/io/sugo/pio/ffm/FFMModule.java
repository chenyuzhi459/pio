package io.sugo.pio.ffm;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;

import java.util.List;

/**
 */
public class FFMModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(new SimpleModule(FFMModule.class.getSimpleName())
                .registerSubtypes(
                        new NamedType(FactorizationMachine.class, "fm"),
                        new NamedType(FieldAwareFactorizationMachine.class, "ffm")
                )
        );
    }

    @Override
    public void configure(Binder binder) {
    }
}
