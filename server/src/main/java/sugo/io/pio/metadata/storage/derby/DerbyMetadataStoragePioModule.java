package sugo.io.pio.metadata.storage.derby;

import com.google.inject.Binder;
import com.google.inject.Key;
import sugo.io.pio.guice.LazySingleton;
import sugo.io.pio.guice.PolyBind;
import sugo.io.pio.guice.SQLMetadataStoragePioModule;
import sugo.io.pio.metadata.MetadataStorageConnector;
import sugo.io.pio.metadata.MetadataStorageProvider;
import sugo.io.pio.metadata.SQLMetadataConnector;

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
