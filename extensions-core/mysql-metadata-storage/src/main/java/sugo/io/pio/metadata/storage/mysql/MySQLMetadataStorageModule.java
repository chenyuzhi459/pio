package sugo.io.pio.metadata.storage.mysql;

import com.fasterxml.jackson.databind.Module;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Key;
import sugo.io.pio.guice.LazySingleton;
import sugo.io.pio.guice.PolyBind;
import sugo.io.pio.guice.SQLMetadataStoragePioModule;
import sugo.io.pio.initialization.PioModule;
import sugo.io.pio.metadata.MetadataStorageConnector;
import sugo.io.pio.metadata.SQLMetadataConnector;

import java.util.List;

/**
 */
public class MySQLMetadataStorageModule extends SQLMetadataStoragePioModule implements PioModule
{
    public static final String TYPE = "mysql";

    public MySQLMetadataStorageModule()
    {
        super(TYPE);
    }

    @Override
    public List<? extends Module> getJacksonModules()
    {
        return ImmutableList.of();
    }

    @Override
    public void configure(Binder binder)
    {
        super.configure(binder);

        PolyBind.optionBinder(binder, Key.get(MetadataStorageConnector.class))
                .addBinding(TYPE)
                .to(MySQLConnector.class)
                .in(LazySingleton.class);

        PolyBind.optionBinder(binder, Key.get(SQLMetadataConnector.class))
                .addBinding(TYPE)
                .to(MySQLConnector.class)
                .in(LazySingleton.class);
    }
}
