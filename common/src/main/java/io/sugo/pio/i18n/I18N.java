package io.sugo.pio.i18n;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.metamx.common.logger.Logger;

import java.io.*;
import java.util.Properties;

public class I18N {
    private static final Logger log = new Logger(I18N.class);

    private static final String languageResource = "resource_zh_CN.properties";

    private static final Properties resourceProps = new Properties();

    public static void loadLanguageResource() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(languageResource);
        log.info("Loading language resource properties from %s", languageResource);
        try {
            resourceProps.load(new InputStreamReader(stream, Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static String getMessage(String key) {
        String value = resourceProps.getProperty(key);
        if (value == null || value.trim() == "") {
            return key;
        }
        return value;
    }
}
