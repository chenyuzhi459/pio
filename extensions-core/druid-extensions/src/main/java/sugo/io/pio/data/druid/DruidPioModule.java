package sugo.io.pio.data.druid;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;
import sugo.io.pio.initialization.PioModule;

import java.util.List;
import java.util.Map;

/**
 */
public class DruidPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(DruidPioModule.class.getSimpleName())
                        .registerSubtypes(new NamedType(DruidBatchEventHose.class, "Druid")));
    }

    @Override
    public void configure(Binder binder) {

    }
}
