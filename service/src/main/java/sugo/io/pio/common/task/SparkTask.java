package sugo.io.pio.common.task;

import com.clearspring.analytics.util.Lists;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import sugo.io.pio.guice.ExtensionsConfig;
import sugo.io.pio.guice.GuiceInjectors;
import sugo.io.pio.initialization.Initialization;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 */
public abstract class SparkTask implements Task {
    private static final ExtensionsConfig extensionsConfig;

    final static Injector injector = GuiceInjectors.makeStartupInjector();

    static {
        extensionsConfig = injector.getInstance(ExtensionsConfig.class);
    }

    private final List<String> sparkDependencyCoordinates;

    protected SparkTask(
            List<String> sparkDependencyCoordinates
    )
    {
        this.sparkDependencyCoordinates = sparkDependencyCoordinates;
    }

    protected void buildClasspath() throws MalformedURLException
    {
        final List<String> finalHadoopDependencyCoordinates = sparkDependencyCoordinates != null
                ? sparkDependencyCoordinates
                : Collections.emptyList();

        final List<URL> jobURLs = Lists.newArrayList(
                Arrays.asList(((URLClassLoader) SparkTask.class.getClassLoader()).getURLs())
        );

        for (final File extension : Initialization.getExtensionFilesToLoad(extensionsConfig)) {
            final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(extension);
            jobURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
        }

        final List<URL> localClassLoaderURLs = new ArrayList<>(jobURLs);

        // hadoop dependencies come before druid classes because some extensions depend on them
        for (final File hadoopDependency :
                Initialization.getHadoopDependencyFilesToLoad(
                        finalHadoopDependencyCoordinates,
                        extensionsConfig
                )) {
            final ClassLoader hadoopLoader = Initialization.getClassLoaderForExtension(hadoopDependency);
            localClassLoaderURLs.addAll(Arrays.asList(((URLClassLoader) hadoopLoader).getURLs()));
        }

        final String hadoopContainerDruidClasspathJars = Joiner.on(File.pathSeparator).join(jobURLs);
        System.setProperty("pio.spark.internal.classpath", hadoopContainerDruidClasspathJars);
    }

}
