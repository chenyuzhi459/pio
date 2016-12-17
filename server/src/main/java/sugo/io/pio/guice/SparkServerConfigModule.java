package sugo.io.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import sugo.io.pio.metadata.SparkServerConfig;

/**
 */
public class SparkServerConfigModule implements Module {
    @Override
    public void configure(Binder binder) {

        JsonConfigProvider.bind(binder,"spark.history", SparkServerConfig.class);

    }
}
