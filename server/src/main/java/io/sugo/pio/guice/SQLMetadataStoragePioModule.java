package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import io.sugo.pio.metadata.*;

/**
 */
public class SQLMetadataStoragePioModule implements Module {
    public static final String PROPERTY = "pio.metadata.storage.type";
    final String type;

    public SQLMetadataStoragePioModule(String type) {
        this.type = type;
    }

    /**
     * This function only needs to be called by the default SQL metadata storage module
     * Other modules should default to calling super.configure(...) alone
     */
    public void createBindingChoices(Binder binder, String defaultPropertyValue) {
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

        PolyBind.createChoiceWithDefault(
                binder,
                PROPERTY,
                Key.get(MetadataStorageActionHandlerFactory.class),
                Key.get(SQLMetadataStorageActionHandlerFactory.class),
                defaultPropertyValue
        );
        PolyBind.createChoiceWithDefault(
                binder,
                PROPERTY,
                Key.get(MetadataRecInstanceManager.class),
                Key.get(SQLMetadataRecInstanceManager.class),
                defaultPropertyValue
        );
    }

    @Override
    public void configure(Binder binder) {

        PolyBind.optionBinder(binder, Key.get(MetadataRecInstanceManager.class))
                .addBinding(type)
                .to(SQLMetadataRecInstanceManager.class)
                .in(LazySingleton.class);
    }
}
