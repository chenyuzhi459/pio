package io.sugo.pio.parameter;


import io.sugo.pio.ports.metadata.MetaData;

import java.io.Serializable;

/**
 * Many parameter types depend on {@link MetaData} arriving at the operator they belong to. This
 * meta data may be different each time the editor component is shown. To that end, the parameter
 * type can use this interface to query the current meta data.
 * <p>
 * GUI components can also add listeners to be informed upon change.
 *
 * @author Simon Fischer
 */
public interface MetaDataProvider extends Serializable {

    /**
     * Returns the meta data currently avaiable.
     */
    public MetaData getMetaData();
}
