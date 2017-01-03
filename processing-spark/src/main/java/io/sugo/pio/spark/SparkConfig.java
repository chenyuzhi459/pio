package io.sugo.pio.spark;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class SparkConfig {
    @JsonProperty
    private String sparkVersion = "2.0.2";

    @JsonProperty
    private String sparkAssemblyJar = "hdfs://";

    @JsonProperty
    private String hadoopConfigPath = "hadoop/";

    public String getSparkVersion() {
        return sparkVersion;
    }

    public String getSparkAssemblyJar() {
        return sparkAssemblyJar;
    }

    public String getHadoopConfigPath() {
        return hadoopConfigPath;
    }
}
