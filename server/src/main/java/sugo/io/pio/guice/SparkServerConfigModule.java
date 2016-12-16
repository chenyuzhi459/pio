package sugo.io.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import sugo.io.pio.metadata.SparkServerConfig;

/**
 * Created by kitty on 16-12-16.
 */
public class SparkServerConfigModule implements Module {
    @Override
    public void configure(Binder binder) {

        JsonConfigProvider.bind(binder,"spark", SparkServerConfig.class);

    }
}
