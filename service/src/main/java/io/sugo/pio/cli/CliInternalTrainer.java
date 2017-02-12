package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.sugo.pio.engine.TrainingWorkFlow;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.spark.JobHelper;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.SparkVersion;
import io.sugo.pio.spark.handler.SparkSubmissionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.deploy.yarn.Client;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 */
@Command(
        name = "internal-trainer",
        description = "Train the engine for pio"
)
public class CliInternalTrainer extends GuiceRunnable {
    private static final Logger log = new Logger(CliTrainer.class);

    private static final String TRAINING_WORKFLOW_CLASSNAME = TrainingWorkFlow.class.getName();

    @Arguments(description = "trainingSpec.json", required = true)
    public String trainingSpec;

    public CliInternalTrainer() {
        super(log);
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
            String sparkClass = TRAINING_WORKFLOW_CLASSNAME;

            Configuration configuration = new Configuration();
            SparkVersion sparkVersion = SparkVersion.getFromId(sparkConfig.getSparkVersion());
            SparkSubmissionHandler ssh = SparkSubmissionHandler.createSubmissionHandler(sparkVersion);
            ssh.setSparkLibsPath(sparkConfig.getSparkAssemblyJar(), false);
            ssh.initConf("trainingApp", sparkClass);
            ssh.setYarnQueue(sparkConfig.getYarnQueue());
            JobHelper.setupClasspath(JobHelper.distributedClassPath(sparkConfig.getWorkingPath()),
                    JobHelper.distributedClassPath(sparkConfig.getIntermediatePath()),
                    configuration,
                    ssh);
            YarnHAUtil.setRMHA(configuration);
            ssh.setUserJar("/home/yaotc/IdeaProjects/pio-sugo/processing-engine-common/target/pio-processing-engine-common-1.0-SNAPSHOT.jar");
            ssh.setUserArguments(trainingSpecJson, null);

            Client client = new Client(ssh.createClientArguments(), configuration, ssh.getSparkConf());
            ApplicationId appId = client.submitApplication();
            short interval = 5000;
            YarnApplicationState state;
            do {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    ;
                }

                ApplicationReport report = client.getApplicationReport(appId);
                state = report.getYarnApplicationState();
            }
            while (state != YarnApplicationState.FINISHED && state != YarnApplicationState.FAILED && state != YarnApplicationState.KILLED);
        } catch (Exception e) {
            log.error(e, "failure!!!!");
            System.exit(1);
        }
    }
}
