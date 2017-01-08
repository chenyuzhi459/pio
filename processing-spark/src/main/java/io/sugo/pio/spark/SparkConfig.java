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
    private List<String> hadoopConfigFiles = new ArrayList<>();

    @JsonProperty
    private String yarnQueue = "root.default";

    @JsonProperty
    private String commonJarLocation;

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

    public List<String> getHadoopConfigFiles() {
        return hadoopConfigFiles;
    }

    public void setHadoopConfigFiles(List<String> hadoopConfigFiles) {
        this.hadoopConfigFiles = hadoopConfigFiles;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }

    public String getCommonJarLocation() {
        return commonJarLocation;
    }

    public void setCommonJarLocation(String commonJarLocation) {
        this.commonJarLocation = commonJarLocation;
    }
}
