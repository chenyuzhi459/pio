package io.sugo.pio.spark.datahandler.mapreducehdfs;

import com.google.common.base.Joiner;
import io.sugo.pio.spark.SparkVersion;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 */
public class SparkSubmissionHandlerV1 extends SparkSubmissionHandler {
    private SparkSubmissionHandlerV1(SparkVersion version) {
        this.sparkVersion = version;
    }

    public static SparkSubmissionHandler create(SparkVersion version) {
        return new SparkSubmissionHandlerV1(version);
    }

    @Override
    protected void initConfSpecific(boolean isWindows, String appName, String sparkClassName) {
    }

    @Override
    public void setSparkLibsPath(String sparkLibsPath, boolean isDirectory) {
        String key = isDirectory?"spark.yarn.jars":"spark.yarn.archive";
        if(isDirectory && !sparkLibsPath.endsWith("/*")) {
            sparkLibsPath = sparkLibsPath + "/*";
        }

        this.sparkConf.set(key, sparkLibsPath);
    }

    @Override
    public void setAdditionalFiles(List<String> additionalFiles) {
        this.sparkConf.set("spark.yarn.dist.files", Joiner.on(",").join(additionalFiles));
    }

    @Override
    protected void setAdditionalJars(List<String> additionalJarsList) {
        this.sparkConf.set("spark.yarn.dist.jars", Joiner.on(",").join(additionalJarsList));
    }


    @Override
    public ClientArguments createClientArguments() {
        try {
            Constructor constructor = ClientArguments.class.getConstructor(new Class[]{String[].class, SparkConf.class});
            Object object = constructor.newInstance(new Object[]{this.getArgumentArray(), sparkConf});
            return (ClientArguments)object;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setExecutorMemory(String executorMemory) {
        addToArgList("--executor-memory", executorMemory);
    }

    @Override
    public void setExecutorInstances(int executorInstances) {
        if(sparkVersion.is15OrAbove()) {
            sparkConf.set("spark.executor.instances", String.valueOf(executorInstances));
        } else {
            addToArgList("--num-executors", String.valueOf(executorInstances));
        }

    }

    @Override
    public void setExecutorCores(int executorCores) {
        addToArgList("--executor-cores", String.valueOf(executorCores));
    }

    @Override
    public void setDriverMemory(String driverMemory) {
        addToArgList("--driver-memory", driverMemory);
    }

    @Override
    public void setDriverCores(int driverCores) {
        addToArgList("--driver-cores", String.valueOf(driverCores));
    }

}
