package io.sugo.pio.spark.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.spark.SparkConfig;

/**
 */
public class SparkConfigModule implements Module {
    @Override
    public void configure(Binder binder) {
        JsonConfigProvider.bind(binder,"pio.spark", SparkConfig.class);
    }
}
