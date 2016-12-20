package sugo.io.pio.common;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import sugo.io.pio.util.YarnHAUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import sugo.io.pio.common.task.BatchTrainingTask;
import sugo.io.pio.common.task.Task;
import sugo.io.pio.deploy.SubmitArgs;
import sugo.io.pio.engine.EngineInstance;
import sugo.io.pio.metadata.AppConfig;
import sugo.io.pio.server.EngineStorage;

import java.io.IOException;
import java.util.*;

public class TaskSubmitter {
    private EngineStorage engineStorage;
    private AppConfig appConfig;

    @Inject
    public TaskSubmitter(EngineStorage engineStorage, AppConfig appConfig) {
        this.engineStorage = engineStorage;
        this.appConfig = appConfig;
    }

    public String submit(ClusterType clusterType, Task task)
            throws IOException, InterruptedException {
        //get engineInstance from engineStorage
        EngineInstance engineInstance = getEngineInstance(task);
        if (engineInstance == null) {
            return null;
        }

        String customJar = engineInstance.getJarPath();
        String customEngine = engineInstance.getClassPath();
        String userJar = appConfig.getUserJar();
        String mainClass = appConfig.getMainClass();
        if (userJar.isEmpty() || mainClass.isEmpty() || customJar.isEmpty() || customEngine.isEmpty()) {
            return null;
        }
        List<String> businessArgs = new ArrayList<>();
        businessArgs.add(SubmitArgs.ENGINE_CLASS + "=" + customEngine);
        String[] args = initAppArgs(userJar, mainClass, businessArgs);
        Configuration hadoopConf = initHadoopConfig();
        SparkConf sparkConf = initSparkConfig(clusterType, customJar);
        return submitApp(args, sparkConf, hadoopConf);
    }

    /**
     * @param task
     * @return
     */
    private EngineInstance getEngineInstance(Task task) {
        if (task == null) {
            return null;
        }

        BatchTrainingTask btTask = null;
        if (task instanceof BatchTrainingTask) {
            btTask = (BatchTrainingTask) task;
        }
        Optional<EngineInstance> oe = engineStorage.get(btTask.getId());

        if (oe.equals(Optional.absent())) {
            return null;
        }
        return oe.get();
    }

    private String submitApp(String[] args, SparkConf sparkConf, Configuration hadoopConf) {
        ClientArguments cArgs = new ClientArguments(args);
        Client client = new Client(cArgs, hadoopConf, sparkConf);
        ApplicationId appId = client.submitApplication();
        return appId.toString();
    }

    /**
     * @param userJar
     * @param mainClass
     * @param businessArgs
     * @return
     */
    private String[] initAppArgs(String userJar, String mainClass, List<String> businessArgs) {
        List<String> args = new LinkedList<>();
        args.add(SubmitArgs.MAIN_CLASS);
        args.add(mainClass);
        args.add(SubmitArgs.USER_JAR);
        args.add(userJar);
        for (String businessArg : businessArgs) {
            args.add(SubmitArgs.ARG);
            args.add(businessArg);
        }
        String[] argsArr = new String[args.size()];
        args.toArray(argsArr);
        return argsArr;
    }

    private static void addHadoopConfig(Configuration config, String filePath) {
        config.addResource(TaskSubmitter.class.getResourceAsStream(filePath));
    }

    private Configuration initHadoopConfig() throws IOException, InterruptedException {
        Configuration config = new Configuration();
        addHadoopConfig(config, "/hadoop/core-site.xml");
        addHadoopConfig(config, "/hadoop/hdfs-site.xml");
        addHadoopConfig(config, "/hadoop/mapred-site.xml");
        addHadoopConfig(config, "/hadoop/yarn-site.xml");

        YarnHAUtil.setRMHA(config);
        return config;
    }

    private static void loadSparkArgs(SparkConf conf, Map<String, Object> configs) {
        if (configs != null) {
            for (Map.Entry<String, Object> otherArg : configs.entrySet()) {
                conf.set(otherArg.getKey(), (String) otherArg.getValue());
            }
        }
    }

    private SparkConf initSparkConfig(ClusterType clusterType, String customJar) {
        SparkConf conf = new SparkConf();
        loadSparkArgs(conf, appConfig.getEnv());
        loadSparkArgs(conf, appConfig.getApp());

        if (ClusterType.YARN == clusterType) {
            System.setProperty(SubmitArgs.SPARK_YARN_MODE, Boolean.TRUE.toString());
            //need cluster mode
            conf.set(SubmitArgs.DEPLOY_MODE, "cluster");
        }
        conf.set(SubmitArgs.SPARK_YARN_DIST_JARS, customJar);
        return conf;
    }
}
