package sugo.io.pio.guice;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import sugo.io.pio.jackson.JacksonModule;

import java.util.Arrays;
import java.util.Collection;

/**
 */
public class GuiceInjectors {
    public static Collection<Module> makeDefaultStartupModules() {
        return ImmutableList.<Module>of(
                new PioGuiceExtensions(),
                new JacksonModule(),
                new PropertiesModule(Arrays.asList("common.runtime.properties", "runtime.properties")),
                new ConfigModule(),
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bind(PioSecondaryModule.class);
                    }
                }
        );
    }

    public static Injector makeStartupInjector() {
        return Guice.createInjector(makeDefaultStartupModules());
    }
}
