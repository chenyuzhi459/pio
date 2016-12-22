package io.sugo.pio.metadata.storage.derby;

import com.google.inject.Binder;
import com.google.inject.Key;
import io.sugo.pio.guice.PolyBind;
import io.sugo.pio.metadata.MetadataStorageProvider;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.SQLMetadataStoragePioModule;
import io.sugo.pio.metadata.MetadataStorageConnector;
import io.sugo.pio.metadata.SQLMetadataConnector;

/**
 */
public class DerbyMetadataStoragePioModule extends SQLMetadataStoragePioModule {
    public DerbyMetadataStoragePioModule() {
        super(TYPE);
    }

    public static final String TYPE = "derby";

    @Override
    public void configure(Binder binder)
    {
        createBindingChoices(binder, TYPE);
        super.configure(binder);

        PolyBind.optionBinder(binder, Key.get(MetadataStorageProvider.class))
                .addBinding(TYPE)
                .to(DerbyMetadataStorageProvider.class)
                .in(LazySingleton.class);

        PolyBind.optionBinder(binder, Key.get(MetadataStorageConnector.class))
                .addBinding(TYPE)
                .to(DerbyConnector.class)
                .in(LazySingleton.class);

        PolyBind.optionBinder(binder, Key.get(SQLMetadataConnector.class))
                .addBinding(TYPE)
                .to(DerbyConnector.class)
                .in(LazySingleton.class);
    }
}
