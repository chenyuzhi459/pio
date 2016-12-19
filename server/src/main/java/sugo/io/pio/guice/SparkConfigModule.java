package sugo.io.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import sugo.io.pio.metadata.AppConfig;

/**
 */
public class SparkConfigModule implements Module {
    @Override
    public void configure(Binder binder) {

        JsonConfigProvider.bind(binder,"pio.spark.conf", AppConfig.class);

    }
}
