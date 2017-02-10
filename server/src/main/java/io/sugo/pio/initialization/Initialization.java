package io.sugo.pio.initialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.metamx.common.ISE;
import com.metamx.common.logger.Logger;
import io.sugo.pio.curator.CuratorModule;
import io.sugo.pio.curator.discovery.DiscoveryModule;
import io.sugo.pio.engine.EngineExtensionModule;
import io.sugo.pio.engine.EngineModule;
import io.sugo.pio.guice.*;
import io.sugo.pio.guice.annotations.Client;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.guice.http.HttpClientModule;
import io.sugo.pio.metadata.storage.derby.DerbyMetadataStoragePioModule;
import io.sugo.pio.server.initialization.jetty.JettyServerModule;
import org.apache.commons.io.FileUtils;
import org.eclipse.aether.artifact.DefaultArtifact;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 */
public class Initialization {
    private static final Logger log = new Logger(Initialization.class);
    private static final ConcurrentMap<File, URLClassLoader> loadersMap = new ConcurrentHashMap<>();

    /**
     * Look for extension modules for the given class from both classpath and extensions directory. A user should never
     * put the same two extensions in classpath and extensions directory, if he/she does that, the one that is in the
     * classpath will be loaded, the other will be ignored.
     *
     * @param config Extensions configuration
     * @param clazz  The class of extension module (e.g., DruidModule)
     *
     * @return A collection that contains distinct extension modules
     */
    public synchronized static <T> Collection<T> getFromExtensions(ExtensionsConfig config, Class<T> clazz)
    {
        final Set<T> retVal = Sets.newHashSet();
        final Set<String> loadedExtensionNames = Sets.newHashSet();

        if (config.searchCurrentClassloader()) {
            for (T module : ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader())) {
                final String moduleName = module.getClass().getCanonicalName();
                if (moduleName == null) {
                    log.warn(
                            "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                            module.getClass().getName()
                    );
                } else if (!loadedExtensionNames.contains(moduleName)) {
                    log.info("Adding classpath extension module [%s] for class [%s]", moduleName, clazz.getName());
                    loadedExtensionNames.add(moduleName);
                    retVal.add(module);
                }
            }
        }

        for (File extension : getExtensionFilesToLoad(config)) {
            log.info("Loading extension [%s] for class [%s]", extension.getName(), clazz.getName());
            try {
                final URLClassLoader loader = getClassLoaderForExtension(extension);
                for (T module : ServiceLoader.load(clazz, loader)) {
                    final String moduleName = module.getClass().getCanonicalName();
                    if (moduleName == null) {
                        log.warn(
                                "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                                module.getClass().getName()
                        );
                    } else if (!loadedExtensionNames.contains(moduleName)) {
                        log.info("Adding local file system extension module [%s] for class [%s]", moduleName, clazz.getName());
                        loadedExtensionNames.add(moduleName);
                        retVal.add(module);
                    }
                }
            }
            catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }

        return retVal;
    }

    /**
     * Look for extension modules for the given class from both classpath and extensions directory. A user should never
     * put the same two extensions in classpath and extensions directory, if he/she does that, the one that is in the
     * classpath will be loaded, the other will be ignored.
     *
     * @param config Engines configuration
     * @param clazz  The class of extension module (e.g., DruidModule)
     *
     * @return A collection that contains distinct extension modules
     */
    private synchronized static <T> Collection<T> getFromEngines(EnginesConfig config, Class<T> clazz)
    {
        final Set<T> retVal = Sets.newHashSet();
        final Set<String> loadedExtensionNames = Sets.newHashSet();

        if (config.searchCurrentClassloader()) {
            for (T module : ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader())) {
                final String moduleName = module.getClass().getCanonicalName();
                if (moduleName == null) {
                    log.warn(
                            "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                            module.getClass().getName()
                    );
                } else if (!loadedExtensionNames.contains(moduleName)) {
                    log.info("Adding classpath extension module [%s] for class [%s]", moduleName, clazz.getName());
                    loadedExtensionNames.add(moduleName);
                    retVal.add(module);
                }
            }
        }

        for (File extension : getEngineFilesToLoad(config)) {
            log.info("Loading extension [%s] for class [%s]", extension.getName(), clazz.getName());
            try {
                final URLClassLoader loader = getClassLoaderForExtension(extension);
                for (T module : ServiceLoader.load(clazz, loader)) {
                    final String moduleName = module.getClass().getCanonicalName();
                    if (moduleName == null) {
                        log.warn(
                                "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                                module.getClass().getName()
                        );
                    } else if (!loadedExtensionNames.contains(moduleName)) {
                        log.info("Adding local file system extension module [%s] for class [%s]", moduleName, clazz.getName());
                        loadedExtensionNames.add(moduleName);
                        retVal.add(module);
                    }
                }
            }
            catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }

        return retVal;
    }

    /**
     * Look for extension modules for the given class from both classpath and extensions directory. A user should never
     * put the same two extensions in classpath and extensions directory, if he/she does that, the one that is in the
     * classpath will be loaded, the other will be ignored.
     *
     * @param config Engines configuration
     * @param clazz  The class of extension module (e.g., DruidModule)
     *
     * @return A collection that contains distinct extension modules
     */
    private synchronized static <T> Collection<T> getFromEngineExtensions(EnginesConfig config, Class<T> clazz)
    {
        final Set<T> retVal = Sets.newHashSet();
        final Set<String> loadedExtensionNames = Sets.newHashSet();

        if (config.searchCurrentClassloader()) {
            for (T module : ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader())) {
                final String moduleName = module.getClass().getCanonicalName();
                if (moduleName == null) {
                    log.warn(
                            "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                            module.getClass().getName()
                    );
                } else if (!loadedExtensionNames.contains(moduleName)) {
                    log.info("Adding classpath extension module [%s] for class [%s]", moduleName, clazz.getName());
                    loadedExtensionNames.add(moduleName);
                    retVal.add(module);
                }
            }
        }

        for (File extension : getEngineExtensionFilesToLoad(config)) {
            log.info("Loading extension [%s] for class [%s]", extension.getName(), clazz.getName());
            try {
                final URLClassLoader loader = getClassLoaderForExtension(extension);
                for (T module : ServiceLoader.load(clazz, loader)) {
                    final String moduleName = module.getClass().getCanonicalName();
                    if (moduleName == null) {
                        log.warn(
                                "Extension module [%s] was ignored because it doesn't have a canonical name, is it a local or anonymous class?",
                                module.getClass().getName()
                        );
                    } else if (!loadedExtensionNames.contains(moduleName)) {
                        log.info("Adding local file system extension module [%s] for class [%s]", moduleName, clazz.getName());
                        loadedExtensionNames.add(moduleName);
                        retVal.add(module);
                    }
                }
            }
            catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }

        return retVal;
    }

    /**
     * Find all the extension files that should be loaded by druid.
     * <p/>
     * If user explicitly specifies druid.extensions.loadList, then it will look for those extensions under root
     * extensions directory. If one of them is not found, druid will fail loudly.
     * <p/>
     * If user doesn't specify druid.extension.toLoad (or its value is empty), druid will load all the extensions
     * under the root extensions directory.
     *
     * @param config ExtensionsConfig configured by druid.extensions.xxx
     *
     * @return an array of druid extension files that will be loaded by druid process
     */
    public static File[] getExtensionFilesToLoad(ExtensionsConfig config)
    {
        final File rootExtensionsDir = new File(config.getDirectory());
        if (!rootExtensionsDir.exists() || !rootExtensionsDir.isDirectory()) {
            log.warn("Root extensions directory [%s] is not a directory!?", rootExtensionsDir);
            return new File[]{};
        }
        File[] extensionsToLoad;
        final List<String> toLoad = config.getLoadList();
        if (toLoad == null) {
            extensionsToLoad = rootExtensionsDir.listFiles();
        } else {
            int i = 0;
            extensionsToLoad = new File[toLoad.size()];
            for (final String extensionName : toLoad) {
                final File extensionDir = new File(rootExtensionsDir, extensionName);
                if (!extensionDir.isDirectory()) {
                    throw new ISE(
                            String.format(
                                    "Extension [%s] specified in \"pio.extensions.loadList\" didn't exist!?",
                                    extensionDir.getAbsolutePath()
                            )
                    );
                }
                extensionsToLoad[i++] = extensionDir;
            }
        }
        return extensionsToLoad == null ? new File[]{} : extensionsToLoad;
    }

    /**
     * Find all the extension files that should be loaded by druid.
     * <p/>
     * If user explicitly specifies druid.extensions.loadList, then it will look for those extensions under root
     * extensions directory. If one of them is not found, druid will fail loudly.
     * <p/>
     * If user doesn't specify druid.extension.toLoad (or its value is empty), druid will load all the extensions
     * under the root extensions directory.
     *
     * @param config ExtensionsConfig configured by druid.extensions.xxx
     *
     * @return an array of druid extension files that will be loaded by druid process
     */
    public static File[] getEngineFilesToLoad(EnginesConfig config)
    {
        final File rootExtensionsDir = new File(config.getEnginesDirectory());
        if (!rootExtensionsDir.exists() || !rootExtensionsDir.isDirectory()) {
            log.warn("Root extensions directory [%s] is not a directory!?", rootExtensionsDir);
            return new File[]{};
        }
        File[] enginesToLoad;
        final List<String> toLoad = config.getLoadList();
        if (toLoad == null) {
            enginesToLoad = rootExtensionsDir.listFiles();
        } else {
            int i = 0;
            enginesToLoad = new File[toLoad.size()];
            for (final String extensionName : toLoad) {
                final File extensionDir = new File(rootExtensionsDir, extensionName);
                if (!extensionDir.isDirectory()) {
                    throw new ISE(
                            String.format(
                                    "Extension [%s] specified in \"pio.extensions.loadList\" didn't exist!?",
                                    extensionDir.getAbsolutePath()
                            )
                    );
                }
                enginesToLoad[i++] = extensionDir;
            }
        }
        return enginesToLoad == null ? new File[]{} : enginesToLoad;
    }

    /**
     * Find all the extension files that should be loaded by druid.
     * <p/>
     * If user explicitly specifies druid.extensions.loadList, then it will look for those extensions under root
     * extensions directory. If one of them is not found, druid will fail loudly.
     * <p/>
     * If user doesn't specify druid.extension.toLoad (or its value is empty), druid will load all the extensions
     * under the root extensions directory.
     *
     * @param config ExtensionsConfig configured by druid.extensions.xxx
     *
     * @return an array of druid extension files that will be loaded by druid process
     */
    public static File[] getEngineExtensionFilesToLoad(EnginesConfig config)
    {
        final File rootExtensionsDir = new File(config.getExtensionsDirectory());
        if (!rootExtensionsDir.exists() || !rootExtensionsDir.isDirectory()) {
            log.warn("Root extensions directory [%s] is not a directory!?", rootExtensionsDir);
            return new File[]{};
        }
        File[] extensionsToLoad = rootExtensionsDir.listFiles();
        return extensionsToLoad == null ? new File[]{} : extensionsToLoad;
    }

    /**
     * Find all the hadoop dependencies that should be loaded by druid
     *
     * @param hadoopDependencyCoordinates e.g.["org.apache.hadoop:hadoop-client:2.3.0"]
     * @param extensionsConfig            ExtensionsConfig configured by druid.extensions.xxx
     *
     * @return an array of hadoop dependency files that will be loaded by druid process
     */
    public static File[] getHadoopFilesToLoad(
            List<String> hadoopDependencyCoordinates,
            ExtensionsConfig extensionsConfig
    )
    {
        final File rootSparkDependenciesDir = new File(extensionsConfig.getHadoopDependenciesDir());
        if (rootSparkDependenciesDir.exists() && !rootSparkDependenciesDir.isDirectory()) {
            throw new ISE("Root Spark dependencies directory [%s] is not a directory!?", rootSparkDependenciesDir);
        }
        final File[] hadoopDependenciesToLoad = new File[hadoopDependencyCoordinates.size()];
        int i = 0;
        for (final String coordinate : hadoopDependencyCoordinates) {
            final DefaultArtifact artifact = new DefaultArtifact(coordinate);
            final File hadoopDependencyDir = new File(rootSparkDependenciesDir, artifact.getArtifactId());
            final File versionDir = new File(hadoopDependencyDir, artifact.getVersion());
            // find the hadoop dependency with the version specified in coordinate
            if (!hadoopDependencyDir.isDirectory() || !versionDir.isDirectory()) {
                throw new ISE(
                        String.format("Spark dependency [%s] didn't exist!?", versionDir.getAbsolutePath())
                );
            }
            hadoopDependenciesToLoad[i++] = versionDir;
        }
        return hadoopDependenciesToLoad;
    }

    /**
     * Find all the hadoop dependencies that should be loaded by druid
     *
     * @param sparkDependencyCoordinates e.g.["org.apache.hadoop:hadoop-client:2.3.0"]
     * @param extensionsConfig            ExtensionsConfig configured by druid.extensions.xxx
     *
     * @return an array of hadoop dependency files that will be loaded by druid process
     */
    public static File[] getSparkFilesToLoad(
            List<String> sparkDependencyCoordinates,
            ExtensionsConfig extensionsConfig
    )
    {
        final File rootSparkDependenciesDir = new File(extensionsConfig.getSparkDependenciesDir());
        if (rootSparkDependenciesDir.exists() && !rootSparkDependenciesDir.isDirectory()) {
            throw new ISE("Root Spark dependencies directory [%s] is not a directory!?", rootSparkDependenciesDir);
        }
        final File[] sparkDependenciesToLoad = new File[sparkDependencyCoordinates.size()];
        int i = 0;
        for (final String coordinate : sparkDependencyCoordinates) {
            final DefaultArtifact artifact = new DefaultArtifact(coordinate);
            final File sparkDependencyDir = new File(rootSparkDependenciesDir, artifact.getArtifactId());
            final File versionDir = new File(sparkDependencyDir, artifact.getVersion());
            // find the hadoop dependency with the version specified in coordinate
            if (!sparkDependencyDir.isDirectory() || !versionDir.isDirectory()) {
                throw new ISE(
                        String.format("Spark dependency [%s] didn't exist!?", versionDir.getAbsolutePath())
                );
            }
            sparkDependenciesToLoad[i++] = versionDir;
        }
        return sparkDependenciesToLoad;
    }


    /**
     * @param extension The File instance of the extension we want to load
     *
     * @return a URLClassLoader that loads all the jars on which the extension is dependent
     *
     * @throws MalformedURLException
     */
    public static URLClassLoader getClassLoaderForExtension(File extension) throws MalformedURLException
    {
        URLClassLoader loader = loadersMap.get(extension);
        if (loader == null) {
            final Collection<File> jars = FileUtils.listFiles(extension, new String[]{"jar"}, false);
            final URL[] urls = new URL[jars.size()];
            int i = 0;
            for (File jar : jars) {
                final URL url = jar.toURI().toURL();
                log.info("added URL[%s]", url);
                urls[i++] = url;
            }
            loadersMap.putIfAbsent(extension, new URLClassLoader(urls, Initialization.class.getClassLoader()));
            loader = loadersMap.get(extension);
        }
        return loader;
    }

    public static Injector makeInjectorWithModules(final Injector baseInjector, Iterable<? extends Module> modules) {
        final ModuleList defaultModules = new ModuleList(baseInjector);
        defaultModules.addModules(
                new LifecycleModule(),
                HttpClientModule.global(),
                new HttpClientModule("pio.broker.http", Client.class),
                new CuratorModule(),
                new AnnouncerModule(),
                new ServerModule(),
                new StorageNodeModule(),
                new DiscoveryModule(),
                new JettyServerModule(),
                new ServerViewModule(),
                new MetadataConfigModule(),
                new DerbyMetadataStoragePioModule(),
                new ProcessPioModule(),
                new ProcessingPioModule(),
                new JacksonConfigManagerModule()
        );

        ModuleList actualModules = new ModuleList(baseInjector);
        actualModules.addModule(PioSecondaryModule.class);
        for (Object module : modules) {
            actualModules.addModule(module);
        }

        Module intermediateModules = Modules.override(defaultModules.getModules()).with(actualModules.getModules());

        ModuleList extensionModules = new ModuleList(baseInjector);
        final ExtensionsConfig extensionsConfig = baseInjector.getInstance(ExtensionsConfig.class);
        for (PioModule module : getFromExtensions(extensionsConfig, PioModule.class)) {
            extensionModules.addModule(module);
        }

        final EnginesConfig enginesConfig = baseInjector.getInstance(EnginesConfig.class);
        for (EngineModule module : getFromEngines(enginesConfig, EngineModule.class)) {
            extensionModules.addModule(module);
        }

        for (EngineExtensionModule module : getFromEngineExtensions(enginesConfig, EngineExtensionModule.class)) {
            extensionModules.addModule(module);
        }

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
            } else if (input instanceof EngineModule) {
                baseInjector.injectMembers(input);
                modules.add(registerJacksonModules(((EngineModule) input)));
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

        private EngineModule registerJacksonModules(EngineModule module)
        {
            for (com.fasterxml.jackson.databind.Module jacksonModule : module.getJacksonModules()) {
                jsonMapper.registerModule(jacksonModule);
            }
            return module;
        }
    }
}
