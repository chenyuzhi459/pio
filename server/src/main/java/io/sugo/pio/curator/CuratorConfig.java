package io.sugo.pio.curator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.validation.constraints.Min;

/**
 */
public class CuratorConfig {
    @JsonProperty("host")
    private String zkHosts = "localhost";

    @JsonProperty("sessionTimeoutMs")
    @Min(0)
    private int zkSessionTimeoutMs = 30000;

    @JsonProperty("compress")
    private boolean enableCompression = true;

    @JsonProperty("acl")
    private boolean enableAcl = false;

    public String getZkHosts()
    {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts)
    {
        this.zkHosts = zkHosts;
    }

    public Integer getZkSessionTimeoutMs()
    {
        return zkSessionTimeoutMs;
    }

    public void setZkSessionTimeoutMs(Integer zkSessionTimeoutMs)
    {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }

    public boolean getEnableCompression()
    {
        return enableCompression;
    }

    public void setEnableCompression(Boolean enableCompression)
    {
        Preconditions.checkNotNull(enableCompression, "enableCompression");
        this.enableCompression = enableCompression;
    }

    public boolean getEnableAcl()
    {
        return enableAcl;
    }

    public void setEnableAcl(Boolean enableAcl)
    {
        Preconditions.checkNotNull(enableAcl, "enableAcl");
        this.enableAcl = enableAcl;
    }
}
