package io.sugo.pio.metadata.storage.postgresql;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Key;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.PolyBind;
import io.sugo.pio.guice.SQLMetadataStoragePioModule;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.metadata.MetadataStorageConnector;
import io.sugo.pio.metadata.SQLMetadataConnector;

import java.util.List;

/**
 */
public class PostgreSQLMetadataStorageModule extends SQLMetadataStoragePioModule implements PioModule
{
    public static final String TYPE = "postgresql";

    public PostgreSQLMetadataStorageModule()
    {
        super(TYPE);
    }

    @Override
    public List<? extends com.fasterxml.jackson.databind.Module> getJacksonModules()
    {
        return ImmutableList.of();
    }

    @Override
    public void configure(Binder binder)
    {
        super.configure(binder);

        PolyBind.optionBinder(binder, Key.get(MetadataStorageConnector.class))
                .addBinding(TYPE)
                .to(PostgreSQLConnector.class)
                .in(LazySingleton.class);

        PolyBind.optionBinder(binder, Key.get(SQLMetadataConnector.class))
                .addBinding(TYPE)
                .to(PostgreSQLConnector.class)
                .in(LazySingleton.class);
    }
}
