package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.IOObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class MetaData implements Serializable {

    /** Maps keys (MD_KEY_...) to values. */
    private final Map<String, Object> keyValueMap = new HashMap<String, Object>();

    private Class<? extends IOObject> dataClass;

    public MetaData(Class<? extends IOObject> dataClass) {
        this(dataClass, Collections.<String, Object> emptyMap());
    }

    public MetaData(Class<? extends IOObject> dataClass, String key, Object value) {
        this(dataClass, Collections.singletonMap(key, value));
    }

    public MetaData(Class<? extends IOObject> dataClass, Map<String, Object> keyValueMap) {
        this.dataClass = dataClass;
        this.keyValueMap.putAll(keyValueMap);
    }
}
