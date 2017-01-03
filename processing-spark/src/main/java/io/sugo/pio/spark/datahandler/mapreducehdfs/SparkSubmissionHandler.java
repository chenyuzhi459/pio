package io.sugo.pio.spark.datahandler.mapreducehdfs;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.sugo.pio.spark.SparkVersion;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 */
public abstract class SparkSubmissionHandler {
    static final Map<String, String> CONCATENABLE_CONF_MAP = new HashMap();
    protected Map<String, String> argumentMap;
    protected SparkVersion sparkVersion;
    protected SparkConf sparkConf;

    public SparkSubmissionHandler() {
        CONCATENABLE_CONF_MAP.put("spark.yarn.dist.archives", "--archives");
        CONCATENABLE_CONF_MAP.put("spark.yarn.dist.files", "--files");
        CONCATENABLE_CONF_MAP.put("spark.yarn.dist.jars", "--addJars");
        argumentMap = new LinkedHashMap();
        sparkConf = new SparkConf();
    }

    public static SparkSubmissionHandler createSubmissionHandler(SparkVersion version) {
        return version.is20OrAbove()?SparkSubmissionHandlerV2.create(version):SparkSubmissionHandlerV1.create(version);
    }

    public void addToArgList(String key, String value) {
        argumentMap.put(key, value);
    }

    public void addToArgList(String key, String... valuesToJoin) {
        argumentMap.put(key, Joiner.on(",").join(valuesToJoin));
    }

    public void initConf(String appName, String sparkClassName) {
        sparkConf.setAppName(appName);
        sparkConf.set("spark.submit.deployMode", "cluster");
        System.setProperty("SPARK_YARN_MODE", "true");
        addToArgList("--class", sparkClassName);
        initConfSpecific(appName, sparkClassName);
    }

    protected abstract void initConfSpecific(String appName, String sparkClassName);

    public abstract void setSparkLibsPath(String var1, boolean isDirectory);

    public void setUserJar(String sparkAppJar) {
        addToArgList("--jar", "hdfs://" + sparkAppJar);
    }

    public abstract void setAdditionalFiles(List<String> additionalJarsList);

    public void setAdditionalJars(List<String> jarDependencyPaths, String radoopCommonJar, String radoopSparkJar) {
        ArrayList additionalJarsList = new ArrayList();
        if(jarDependencyPaths != null && !jarDependencyPaths.isEmpty()) {
            LinkedHashSet jars = new LinkedHashSet();
            Iterator<String> iterator = jarDependencyPaths.iterator();

            while(iterator.hasNext()) {
                String depPath = iterator.next();
                jars.add(depPath.startsWith("hdfs://") ? depPath:"hdfs://" + depPath);
            }

            additionalJarsList.addAll(jars);
        }

        additionalJarsList.add("hdfs://" + radoopCommonJar);
        if(radoopSparkJar != null) {
            additionalJarsList.add("hdfs://" + radoopSparkJar);
        }

        if(!additionalJarsList.isEmpty()) {
            setAdditionalJars(additionalJarsList);
        }

    }

    protected abstract void setAdditionalJars(List<String> additionalJarsList);

    public void setUserArguments(String commonParams, String params) {
        if(commonParams != null && !commonParams.isEmpty()) {
            addToArgList("--arg_common", commonParams);
        }

        if(params != null && !params.isEmpty()) {
            addToArgList("--arg_spec", params);
        }

    }

    public abstract void setExecutorMemory(String executorMemory);

    public abstract void setExecutorInstances(int executorInstances);

    public abstract void setExecutorCores(int executorCores);

    public abstract void setDriverMemory(String driverMemory);

    public abstract void setDriverCores(int executorCores);

    public SparkConf getSparkConf() {
        return sparkConf;
    }

    protected String[] getArgumentArray() {
        ArrayList argumentList = new ArrayList();
        Iterator<Map.Entry<String, String>> iterator = argumentMap.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, String> argument = iterator.next();
            String key = argument.getKey();
            if("--arg_common".equals(key) || "--arg_spec".equals(key)) {
                key = "--arg";
            }

            argumentList.add(key);
            argumentList.add(argument.getValue());
        }

        return (String[])argumentList.toArray(new String[0]);
    }

    public abstract ClientArguments createClientArguments();
}