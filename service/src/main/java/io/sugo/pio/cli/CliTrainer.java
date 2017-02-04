package io.sugo.pio.cli;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.sugo.pio.engine.TrainingWorkFlow;
import io.sugo.pio.guice.EnginesConfig;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.initialization.Initialization;
import io.sugo.pio.spark.JobHelper;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.SparkVersion;
import io.sugo.pio.spark.handler.SparkSubmissionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.deploy.yarn.Client;

import java.io.File;
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
public class CliTrainer extends GuiceRunnable {
    private static final Logger log = new Logger(CliTrainer.class);

    @Arguments(description = "trainingSpec.json", required = true)
    public String trainingSpec;

    @Inject
    private EnginesConfig enginesConfig = null;

    public CliTrainer() {
        super(log);
    }

    @Override
    public void run() {
        try {
            Injector injector = makeInjector();

            SparkConfig sparkConfig = injector.getInstance(SparkConfig.class);
            File file = new File(trainingSpec);
            if (!file.exists()) {
                log.error("Could not find the json file");
                System.exit(1);
            }

            String trainingSpecJson = FileUtils.readFileToString(file);
            String sparkClass = TrainingWorkFlow.class.getName();
            URL jarUrl = TrainingWorkFlow.class.getProtectionDomain().getCodeSource().getLocation();
            final List<URL> extensionURLs = Lists.newArrayList();
            for (final File extension : Initialization.getEngineFilesToLoad(enginesConfig)) {
                final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(extension);
                extensionURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
            }

            final List<URL> nonHadoopURLs = Lists.newArrayList();
            nonHadoopURLs.addAll(Arrays.asList(((URLClassLoader) TrainingWorkFlow.class.getClassLoader()).getURLs()));

            final List<URL> jobUrls = Lists.newArrayList();
            jobUrls.addAll(nonHadoopURLs);
            jobUrls.addAll(extensionURLs);
            System.setProperty("pio.spark.internal.classpath", Joiner.on(File.pathSeparator).join(jobUrls));
            SparkVersion sparkVersion = SparkVersion.getFromId(sparkConfig.getSparkVersion());
            SparkSubmissionHandler ssh = SparkSubmissionHandler.createSubmissionHandler(sparkVersion);

            ssh.setSparkLibsPath(sparkConfig.getSparkAssemblyJar(), false);
            ssh.initConf("trainingApp", sparkClass);
            Configuration configuration = new Configuration();
            JobHelper.setupClasspath(JobHelper.distributedClassPath(sparkConfig.getWorkingPath()),
                    JobHelper.distributedClassPath(sparkConfig.getIntermediatePath()),
                    configuration,
                    ssh);
            ssh.setUserJar(jarUrl.getFile());
            ssh.setUserArguments(trainingSpecJson, null);
            Client client = new Client(ssh.createClientArguments(), configuration, ssh.getSparkConf());
            client.submitApplication();
        } catch (Exception e) {
            log.error(e, "failure!!!!");
            System.exit(1);
        }
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(new Module() {
            @Override
            public void configure(Binder binder) {
                JsonConfigProvider.bind(binder, "pio.spark", SparkConfig.class);
            }
        });
    }
}
