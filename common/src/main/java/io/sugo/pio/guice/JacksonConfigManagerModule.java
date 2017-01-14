package io.sugo.pio.guice;

import com.google.common.base.Supplier;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.metamx.common.lifecycle.Lifecycle;
import io.sugo.pio.common.config.ConfigManager;
import io.sugo.pio.common.config.ConfigManagerConfig;
import io.sugo.pio.common.config.JacksonConfigManager;
import io.sugo.pio.metadata.MetadataStorageConnector;
import io.sugo.pio.metadata.MetadataStorageTablesConfig;

/**
 */
public class JacksonConfigManagerModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        JsonConfigProvider.bind(binder, "pio.manager.config", ConfigManagerConfig.class);
        binder.bind(JacksonConfigManager.class).in(LazySingleton.class);
    }

    @Provides
    @ManageLifecycle
    public ConfigManager getConfigManager(
            final MetadataStorageConnector dbConnector,
            final Supplier<MetadataStorageTablesConfig> dbTables,
            final Supplier<ConfigManagerConfig> config,
            final Lifecycle lifecycle
    )
    {
        lifecycle.addHandler(
                new Lifecycle.Handler()
                {
                    @Override
                    public void start() throws Exception
                    {
                        dbConnector.createConfigTable();
                    }

                    @Override
                    public void stop()
                    {

                    }
                }
        );

        return new ConfigManager(dbConnector, dbTables, config);
    }
}
