package io.sugo.pio.spark.datahandler.mapreducehdfs;

import com.google.common.base.Joiner;
import io.sugo.pio.spark.SparkVersion;
import org.apache.spark.deploy.yarn.ClientArguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 */
public class SparkSubmissionHandlerV2 extends SparkSubmissionHandler {
    private SparkSubmissionHandlerV2(SparkVersion version) {
        this.sparkVersion = version;
    }

    public static SparkSubmissionHandler create(SparkVersion version) {
        return new SparkSubmissionHandlerV2(version);
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

        sparkConf.set(key, sparkLibsPath);
    }

    @Override
    public void setAdditionalFiles(List<String> additionalFiles) {
        sparkConf.set("spark.yarn.dist.files", Joiner.on(",").join(additionalFiles));
    }

    @Override
    protected void setAdditionalJars(List<String> additionalJarsList) {
        sparkConf.set("spark.yarn.dist.jars", Joiner.on(",").join(additionalJarsList));
    }

    @Override
    public ClientArguments createClientArguments() {
        try {
            Constructor constructor = ClientArguments.class.getConstructor(new Class[]{String[].class});
            Object object = constructor.newInstance(new Object[]{getArgumentArray()});
            return (ClientArguments)object;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setExecutorMemory(String executorMemory) {
        sparkConf.set("spark.executor.memory", executorMemory);
    }

    @Override
    public void setExecutorInstances(int executorInstances) {
        sparkConf.set("spark.executor.instances", String.valueOf(executorInstances));
    }

    @Override
    public void setExecutorCores(int executorCores) {
        sparkConf.set("spark.executor.cores", String.valueOf(executorCores));
    }

    @Override
    public void setDriverMemory(String driverMemory) {
        sparkConf.set("spark.driver.memory", driverMemory);
    }

    @Override
    public void setDriverCores(int driverCores) {
        sparkConf.set("spark.driver.cores", String.valueOf(driverCores));
    }
}
