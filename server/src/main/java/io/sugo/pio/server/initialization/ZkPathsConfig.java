package io.sugo.pio.server.initialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.curator.utils.ZKPaths;

/**
 */
public class ZkPathsConfig {
    @JsonProperty
    private
    String base = "pio";

    public String getBase()
    {
        return base;
    }

    public String defaultPath(final String subPath)
    {
        return ZKPaths.makePath(getBase(), subPath);
    }
}
