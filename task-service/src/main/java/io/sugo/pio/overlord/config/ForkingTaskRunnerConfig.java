package io.sugo.pio.overlord.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.sugo.pio.guice.TaskServiceModuleHelper;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 */
public class ForkingTaskRunnerConfig {
    public static final String JAVA_OPTS_PROPERTY = TaskServiceModuleHelper.INDEXER_RUNNER_PROPERTY_PREFIX
            + ".javaOpts";
    public static final String JAVA_OPTS_ARRAY_PROPERTY = TaskServiceModuleHelper.INDEXER_RUNNER_PROPERTY_PREFIX
            + ".javaOptsArray";

    @JsonProperty
    @NotNull
    private String javaCommand = "java";

    /**
     * This is intended for setting -X parameters on the underlying java.  It is used by first splitting on whitespace,
     * so it cannot handle properties that have whitespace in the value.  Those properties should be set via a
     * druid.indexer.fork.property. property instead.
     */
    @JsonProperty
    @NotNull
    private String javaOpts = "";

    @JsonProperty
    @NotNull
    private List<String> javaOptsArray = ImmutableList.of();

    @JsonProperty
    @NotNull
    private String classpath = System.getProperty("java.class.path");

    @JsonProperty
    @Min(1024)
    @Max(65535)
    private int startPort = 8100;

    @JsonProperty
    @NotNull
    List<String> allowedPrefixes = Lists.newArrayList(
            "user.timezone",
            "file.encoding",
            "java.io.tmpdir"
    );

    public String getJavaCommand()
    {
        return javaCommand;
    }

    public String getJavaOpts()
    {
        return javaOpts;
    }

    public List<String> getJavaOptsArray()
    {
        return javaOptsArray;
    }

    public String getClasspath()
    {
        return classpath;
    }

    public int getStartPort()
    {
        return startPort;
    }

    public List<String> getAllowedPrefixes()
    {
        return allowedPrefixes;
    }
}
