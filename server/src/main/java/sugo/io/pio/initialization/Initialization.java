package sugo.io.pio.initialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.metamx.common.ISE;
import sugo.io.pio.guice.LifecycleModule;
import sugo.io.pio.guice.MetadataConfigModule;
import sugo.io.pio.guice.PioSecondaryModule;
import sugo.io.pio.guice.annotations.Json;
import sugo.io.pio.initialization.jetty.JettyServerModule;
import sugo.io.pio.metadata.storage.derby.DerbyMetadataStoragePioModule;

import java.util.Collections;
import java.util.List;

/**
 */
public class Initialization {
    public static Injector makeInjectorWithModules(final Injector baseInjector, Iterable<? extends Module> modules)
    {
        final ModuleList defaultModules = new ModuleList(baseInjector);
        defaultModules.addModules(
            new LifecycleModule(),
            new JettyServerModule(),
            new MetadataConfigModule(),
            new DerbyMetadataStoragePioModule()
        );

        ModuleList actualModules = new ModuleList(baseInjector);
        actualModules.addModule(PioSecondaryModule.class);
        for (Object module : modules) {
            actualModules.addModule(module);
        }

        Module intermediateModules = Modules.override(defaultModules.getModules()).with(actualModules.getModules());

        ModuleList extensionModules = new ModuleList(baseInjector);
//        final ExtensionsConfig config = baseInjector.getInstance(ExtensionsConfig.class);
//        for (PioModule module : Initialization.getFromExtensions(config, PioModule.class)) {
//            extensionModules.addModule(module);
//        }

        return Guice.createInjector(Modules.override(intermediateModules).with(extensionModules.getModules()));
    }

    private static class ModuleList
    {
        private final Injector baseInjector;
        private final ObjectMapper jsonMapper;
        private final List<Module> modules;

        public ModuleList(Injector baseInjector)
        {
            this.baseInjector = baseInjector;
            this.jsonMapper = baseInjector.getInstance(Key.get(ObjectMapper.class, Json.class));
            this.modules = Lists.newArrayList();
        }

        private List<Module> getModules()
        {
            return Collections.unmodifiableList(modules);
        }

        public void addModule(Object input)
        {
            if (input instanceof PioModule) {
                baseInjector.injectMembers(input);
                modules.add(registerJacksonModules(((PioModule) input)));
            } else if (input instanceof Module) {
                baseInjector.injectMembers(input);
                modules.add((Module) input);
            } else if (input instanceof Class) {
                if (PioModule.class.isAssignableFrom((Class) input)) {
                    modules.add(registerJacksonModules(baseInjector.getInstance((Class<? extends PioModule>) input)));
                } else if (Module.class.isAssignableFrom((Class) input)) {
                    modules.add(baseInjector.getInstance((Class<? extends Module>) input));
                    return;
                } else {
                    throw new ISE("Class[%s] does not implement %s", input.getClass(), Module.class);
                }
            } else {
                throw new ISE("Unknown module type[%s]", input.getClass());
            }
        }

        public void addModules(Object... object)
        {
            for (Object o : object) {
                addModule(o);
            }
        }

        private PioModule registerJacksonModules(PioModule module)
        {
            for (com.fasterxml.jackson.databind.Module jacksonModule : module.getJacksonModules()) {
                jsonMapper.registerModule(jacksonModule);
            }
            return module;
        }
    }
}
