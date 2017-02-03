package io.sugo.pio.cli;

import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.sugo.pio.engine.TrainingWorkFlow;
import io.sugo.pio.guice.EnginesConfig;
import io.sugo.pio.spark.JobHelper;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.SparkVersion;
import io.sugo.pio.spark.handler.SparkSubmissionHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.deploy.yarn.Client;

import java.net.URL;

/**
 */
@Command(
        name = "trainer",
        description = "Train the engine for pio"
)
public class CliTrainer implements Runnable {
    private static final Logger log = new Logger(CliTrainer.class);

    @Inject
    private EnginesConfig enginesConfig = null;

    @Inject
    private SparkConfig sparkConfig = null;

    @Override
    public void run() {
        try {
            String sparkClass = TrainingWorkFlow.class.getName();
            System.out.println(sparkClass);
            URL jarUrl = TrainingWorkFlow.class.getProtectionDomain().getCodeSource().getLocation();
//            final List<URL> extensionURLs = Lists.newArrayList();
//            for (final File extension : Initialization.getEngineFilesToLoad(enginesConfig)) {
//                final ClassLoader extensionLoader = Initialization.getClassLoaderForExtension(extension);
//                extensionURLs.addAll(Arrays.asList(((URLClassLoader) extensionLoader).getURLs()));
//            }
//
//            final List<URL> nonHadoopURLs = Lists.newArrayList();
//            nonHadoopURLs.addAll(Arrays.asList(((URLClassLoader) TrainingWorkFlow.class.getClassLoader()).getURLs()));
//
//            final List<URL> jobUrls = Lists.newArrayList();
//            jobUrls.addAll(nonHadoopURLs);
//            jobUrls.addAll(extensionURLs);
//
//            System.setProperty("pio.spark.internal.classpath", Joiner.on(File.pathSeparator).join(jobUrls));
//
            SparkVersion sparkVersion = SparkVersion.getFromId(sparkConfig.getSparkVersion());
            SparkSubmissionHandler ssh = SparkSubmissionHandler.createSubmissionHandler(sparkVersion);
            ssh.initConf("trainingApp", sparkClass);
            Configuration configuration = new Configuration();
            JobHelper.setupClasspath(JobHelper.distributedClassPath(sparkConfig.getWorkingPath()),
                    JobHelper.distributedClassPath(sparkConfig.getIntermediatePath()),
                    configuration,
                    ssh);
            ssh.setUserJar(jarUrl.getFile());
            Client client = new Client(ssh.createClientArguments(), configuration, ssh.getSparkConf());
            client.submitApplication();
        } catch (Exception e) {
            log.error(e, "failure!!!!");
            System.exit(1);
        }
    }
}
