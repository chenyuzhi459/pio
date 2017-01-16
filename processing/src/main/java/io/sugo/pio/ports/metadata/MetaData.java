package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.OutputPort;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 */
public class MetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** A list of ports that have generated or modified this meta data. */
    private transient LinkedList<OutputPort> generationHistory = new LinkedList<OutputPort>();

    /** Maps keys (MD_KEY_...) to values. */
    private final Map<String, Object> keyValueMap = new HashMap<String, Object>();

    private Class<? extends IOObject> dataClass;

    private Annotations annotations = new Annotations();

    public MetaData() {
        this(IOObject.class);
    }

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

    public Class<? extends IOObject> getObjectClass() {
        return dataClass;
    }

    public void addToHistory(OutputPort generator) {
        this.generationHistory.addFirst(generator);
    }

    @Override
    public MetaData clone() {
        MetaData clone;
        try {
            clone = this.getClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot clone " + this, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot clone " + this, e);
        }
        if (generationHistory == null) {
            clone.generationHistory = new LinkedList<OutputPort>();
        } else {
            clone.generationHistory = new LinkedList<OutputPort>(generationHistory);
        }
        clone.dataClass = this.getObjectClass();
        clone.keyValueMap.putAll(this.keyValueMap);
        if (annotations != null) {
            clone.annotations.putAll(annotations);
        }
        return clone;
    }

    public String getDescription() {
        String name = dataClass.getSimpleName();
        StringBuilder desc = new StringBuilder(name);
        if (!keyValueMap.isEmpty()) {
            desc.append("; ");
            desc.append(keyValueMap);
        }
        if ((annotations != null) && !annotations.isEmpty()) {
            desc.append("<ul>");
            for (String key : annotations.getKeys()) {
                desc.append("<li><em>").append(key).append(":</em> ").append(annotations.get(key));
            }
            desc.append("</ul>");
        }
        return desc.toString();
    }

}
