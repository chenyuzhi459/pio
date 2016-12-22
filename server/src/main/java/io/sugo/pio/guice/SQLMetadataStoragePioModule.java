package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import io.sugo.pio.metadata.MetadataStorageProvider;
import io.sugo.pio.metadata.MetadataStorageConnector;
import io.sugo.pio.metadata.NoopMetadataStorageProvider;
import io.sugo.pio.metadata.SQLMetadataConnector;

/**
 */
public class SQLMetadataStoragePioModule implements Module {
    public static final String PROPERTY = "pio.metadata.storage.type";
    final String type;

    public SQLMetadataStoragePioModule(String type)
    {
        this.type = type;
    }

    /**
     * This function only needs to be called by the default SQL metadata storage module
     * Other modules should default to calling super.configure(...) alone
     */
    public void createBindingChoices(Binder binder, String defaultPropertyValue)
    {
        PolyBind.createChoiceWithDefault(
                binder, PROPERTY, Key.get(MetadataStorageConnector.class), null, defaultPropertyValue
        );
        PolyBind.createChoiceWithDefault(
                binder,
                PROPERTY,
                Key.get(MetadataStorageProvider.class),
                Key.get(NoopMetadataStorageProvider.class),
                defaultPropertyValue
        );
        PolyBind.createChoiceWithDefault(
                binder, PROPERTY, Key.get(SQLMetadataConnector.class), null, defaultPropertyValue
        );
    }

    @Override
    public void configure(Binder binder) {

    }
}
