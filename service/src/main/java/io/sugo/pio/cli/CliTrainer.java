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
import org.apache.hadoop.conf.Configuration;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 */
@Command(
        name = "trainer",
        description = "Train the engine for pio"
)
public class CliTrainer implements Runnable {
    private static final Logger log = new Logger(CliTrainer.class);

    private final List<String> finalSparkDependencyCoordinates = ImmutableList.of(
            "org.apache.spark:spark-yarn_2.11:2.0.2"
    );

    private final List<String> finalHadoopDependencyCoordinates = ImmutableList.of(
            "org.apache.hadoop:hadoop-client:2.7.2"
    );

    @Arguments(description = "trainingSpec.json", required = true)
    public String trainingSpec;

    @Inject
    private ExtensionsConfig extensionsConfig = null;

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

            final List<URL> jobUrls = Lists.newArrayList();
            jobUrls.addAll(engineURLs);
            jobUrls.addAll(extensionURLs);
            System.setProperty("pio.spark.internal.classpath", Joiner.on(File.pathSeparator).join(jobUrls));

            final List<URL> nonHadoopURLs = Lists.newArrayList();
            nonHadoopURLs.addAll(Arrays.asList(((URLClassLoader) CliTrainer.class.getClassLoader()).getURLs()));

            final List<URL> driverURLs = Lists.newArrayList();
            driverURLs.addAll(nonHadoopURLs);
            // put spark dependencies last to avoid jets3t & apache.httpcore version conflicts
            for (final File dependency :
                    Initialization.getSparkFilesToLoad(
                            finalSparkDependencyCoordinates,
                            extensionsConfig
                    )) {
                final ClassLoader sparkLoader = Initialization.getClassLoaderForExtension(dependency);
                driverURLs.addAll(Arrays.asList(((URLClassLoader) sparkLoader).getURLs()));
            }

            final URLClassLoader loader = new URLClassLoader(driverURLs.toArray(new URL[driverURLs.size()]), null);
            Thread.currentThread().setContextClassLoader(loader);

            final Class<?> mainClass = loader.loadClass(Main.class.getName());
            final Method mainMethod = mainClass.getMethod("main", String[].class);

            String[] args = new String[]{
                    "internal",
                    "internal-trainer",
                    trainingSpec
            };
            mainMethod.invoke(null, new Object[]{args});

        } catch (Exception e) {
            log.error(e, "failure!!!!");
            System.exit(1);
        }
    }
}
