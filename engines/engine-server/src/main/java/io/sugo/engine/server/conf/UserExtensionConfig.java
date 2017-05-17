package io.sugo.engine.server.conf;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by penghuan on 2017/4/25.
 */
public class UserExtensionConfig {

    @JsonProperty
//    private String url = "http://192.168.0.212:8030/api/query-druid?qs=";
    private String url;

    @JsonProperty
    private String repository = "repositories/user_extension";

    @JsonProperty
    private String sparkJobServerHost;

    @JsonProperty
    private Integer sparkJobServerPort;

    @JsonProperty
    private String sparkJobServerContext;

    @JsonProperty
    private String sparkJobServerAppName;

    @JsonProperty
    private String sparkJobServerClassPath;

    public String getUrl() {
        return url;
    }

    public String getRepository() {
        return repository;
    }

    public String getSparkJobServerHost() {
        return sparkJobServerHost;
    }

    public Integer getSparkJobServerPort() {
        return sparkJobServerPort;
    }

    public String getSparkJobServerContext() {
        return sparkJobServerContext;
    }

    public String getSparkJobServerAppName() {
        return sparkJobServerAppName;
    }

    public String getSparkJobServerClassPath() {
        return sparkJobServerClassPath;
    }

    @Override
    public String toString() {
        return String.format("url:[%s],repository:[%s],sparkJobServer[%s:%d],sparkJob[%s:%s:%s]",
                url, repository, sparkJobServerHost, sparkJobServerPort,
                sparkJobServerContext, sparkJobServerAppName, sparkJobServerClassPath);
    }

}
