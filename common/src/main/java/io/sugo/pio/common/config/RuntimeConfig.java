package io.sugo.pio.common.config;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.metamx.common.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class RuntimeConfig {

    private static final Logger log = new Logger(RuntimeConfig.class);

    private static final String runtimeResource = "common.runtime.properties";

    private static final Properties runtimeProps = new Properties();

    public static void loadResource() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(runtimeResource);
        log.info("Loading runtime resource properties from %s", runtimeResource);
        try {
            runtimeProps.load(new InputStreamReader(stream, Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static String get(String key) {
        return runtimeProps.getProperty(key);
    }

}
