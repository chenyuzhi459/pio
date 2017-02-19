package io.sugo.pio.cli;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.sugo.pio.guice.EnginesConfig;
import io.sugo.pio.guice.ExtensionsConfig;
import io.sugo.pio.initialization.Initialization;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 */
@Command(
        name = "peon",
        description = "Runs a Peon, this is an individual forked \"task\" used as part of the indexing service. "
                + "This should rarely, if ever, be used directly."
)
public class CliPeon implements Runnable {
    private static final Logger log = new Logger(CliPeon.class);

    @Arguments(description = "task.json status.json", required = true)
    public List<String> taskAndStatusFile;

    @Inject
    private EnginesConfig enginesConfig = null;

    @Override
    public void run() {
        try {

            final List<URL> engineURLs = Lists.newArrayList();
            for (final File engineFile : Initialization.getEngineFilesToLoad(enginesConfig)) {
                final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(engineFile);
                engineURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
            }

            final List<URL> extensionURLs = Lists.newArrayList();
            for (final File extension : Initialization.getEngineExtensionFilesToLoad(enginesConfig)) {
                final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(extension);
                extensionURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
            }

            final List<URL> driverURLs = Lists.newArrayList();
            driverURLs.addAll(engineURLs);
            driverURLs.addAll(extensionURLs);
            driverURLs.addAll(Arrays.asList(((URLClassLoader) CliPeon.class.getClassLoader()).getURLs()));

            final URLClassLoader loader = new URLClassLoader(driverURLs.toArray(new URL[driverURLs.size()]), null);
            Thread.currentThread().setContextClassLoader(loader);

            final Class<?> mainClass = loader.loadClass(Main.class.getName());
            final Method mainMethod = mainClass.getMethod("main", String[].class);

            String[] args = new String[]{
                    "internal",
                    "internal-peon",
                    taskAndStatusFile.get(0),
                    taskAndStatusFile.get(1)
            };
            mainMethod.invoke(null, new Object[]{args});

        } catch (Exception e) {
            log.error(e, "failure!!!!");
            System.exit(1);
        }
    }
}
