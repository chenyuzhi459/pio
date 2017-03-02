package io.sugo.pio.i18n;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.metamx.common.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class I18N {
    private static final Logger log = new Logger(I18N.class);

    private static final String languageResource = "resource_zh_CN.properties";

    private static final String errorResource = "error_zh_CN.properties";

    private static final Properties resourceProps = new Properties();

    private static final Properties errorProps = new Properties();

    public static void loadResources() {
        loadLanguageResource();
        loadErrorResource();
    }

    private static void loadLanguageResource() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(languageResource);
        log.info("Loading language resource properties from %s", languageResource);
        try {
            resourceProps.load(new InputStreamReader(stream, Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static void loadErrorResource() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(errorResource);
        log.info("Loading error resource properties from %s", errorResource);
        try {
            errorProps.load(new InputStreamReader(stream, Charsets.UTF_8));
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

    public static String getErrorMessage(String key) {
        String value = errorProps.getProperty(key);
        if (value == null || value.trim() == "") {
            return key;
        }
        return value;
    }
}
