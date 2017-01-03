package io.sugo.pio.spark;

import com.fasterxml.jackson.databind.Module;

import java.util.List;

/**
 */
public interface SparkModule {
    public List<? extends Module> getJacksonModules();
}
