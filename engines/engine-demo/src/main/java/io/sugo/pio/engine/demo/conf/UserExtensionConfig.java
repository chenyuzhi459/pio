package io.sugo.pio.engine.demo.conf;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by penghuan on 2017/4/12.
 */
public class UserExtensionConfig {

    @JsonProperty
//    private String url = "http://192.168.0.212:8030/api/query-druid?qs=";
    private String url;

    @JsonProperty
    private String repository = "repositories/user_extension";

    public String getUrl() {
        return url;
    }

    public String getRepository() {
        return repository;
    }

    @Override
    public String toString() {
        return String.format("url:[%s],repository:[%s]", url, repository);
    }

}
