package io.sugo.pio.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.sugo.pio.engine.EngineModule;
import io.sugo.pio.guice.EnginesConfig;
import io.sugo.pio.guice.ExtensionsConfig;
import io.sugo.pio.initialization.Initialization;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

/**
 */
@Command(
        name = "trainer",
        description = "Train the engine for pio"
)
public class CliTrainer extends GuiceRunnable {
    @Arguments(description = "engine.json", required = true)
    private String engineFile;

    private static final Logger log = new Logger(CliTrainer.class);

    @Inject
    private EnginesConfig enginesConfig = null;


    public CliTrainer() {
        super(log);
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module()
                {
                    @Override
                    public void configure(Binder binder)
                    {
                        binder.bindConstant().annotatedWith(Names.named("serviceName")).to("pio/trainer");
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
                    }
                }
        );
    }

    @Override
    public void run() {
        final List<URL> extensionURLs = Lists.newArrayList();
        for (final File extension : Initialization.getEngineFilesToLoad(enginesConfig)) {
//            System.out.println(extension);
//            try {
//                final URLClassLoader loader = Initialization.getClassLoaderForExtension(extension);
//                for (EngineModule module : ServiceLoader.load(EngineModule.class, loader)) {
//                    final String moduleName = module.getClass().getCanonicalName();
//                    System.out.println(moduleName);
//                }
//            }
//            catch (Exception e) {
//                throw Throwables.propagate(e);
//            }
            try {
                final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(extension);
                for (EngineModule module : ServiceLoader.load(EngineModule.class, extensionLoader)) {
                    System.out.println(module.getEngineName());
                    extensionURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
                }

            } catch (Exception e) {
                throw Throwables.propagate(e);
            }

        }
        System.out.println(extensionURLs);
//        System.out.println(EngineModule.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
