package io.sugo.pio.spark;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SparkConfig {
    @JsonProperty
    private String sparkVersion = "spark_2.0";

    @JsonProperty
    private String sparkAssemblyJar = "hdfs://";

    @JsonProperty
    private String yarnQueue = "root.default";

    @JsonProperty
    private String intermediatePath = "hdfs://";

    @JsonProperty
    private String workingPath = "hdfs://";

    public String getSparkVersion() {
        return sparkVersion;
    }

    public void setSparkVersion(String sparkVersion) {
        this.sparkVersion = sparkVersion;
    }

    public String getSparkAssemblyJar() {
        return sparkAssemblyJar;
    }

    public void setSparkAssemblyJar(String sparkAssemblyJar) {
        this.sparkAssemblyJar = sparkAssemblyJar;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }

    public String getWorkingPath() {
        return workingPath;
    }

    public void setWorkingPath(String workingPath) {
        this.workingPath = workingPath;
    }

    public String getIntermediatePath() {
        return intermediatePath;
    }

    public void setIntermediatePath(String intermediatePath) {
        this.intermediatePath = intermediatePath;
    }
}
