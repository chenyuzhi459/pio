package sugo.io.pio.druid;

import com.fasterxml.jackson.databind.Module;
import com.google.inject.Binder;
import sugo.io.pio.initialization.PioModule;

import java.util.List;

/**
 */
public class DruidPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return null;
    }

    @Override
    public void configure(Binder binder) {

    }
}
