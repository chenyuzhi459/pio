package io.sugo.pio.ports.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.io.Serializable;
import java.util.*;

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

    @JsonProperty
    public String getObjectClassName(){
        return dataClass.getName();
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

    /**
     * Returns true if isData is compatible with this meta data, where <code>this</code> represents
     * desired meta data and isData represents meta data that was actually delivered.
     */
    public boolean isCompatible(MetaData isData, CompatibilityLevel level) {
        return getErrorsForInput(null, isData, level).isEmpty();
    }

    /**
     * Returns a (possibly empty) list of errors specifying in what regard <code>isData</code>
     * differs from <code>this</code> meta data specification.
     *
     * @param inputPort
     *            required for generating errors
     * @param isData
     *            the data received by the port
     */
    public Collection<MetaDataError> getErrorsForInput(InputPort inputPort, MetaData isData, CompatibilityLevel level) {
        if (!this.dataClass.isAssignableFrom(isData.dataClass)) {
            return Collections.<MetaDataError> singletonList(new InputMissingMetaDataError(inputPort, this.getObjectClass(),
                    isData.getObjectClass()));
        }
        Collection<MetaDataError> errors = new LinkedList<MetaDataError>();
        if (level == CompatibilityLevel.VERSION_5) {
            for (Map.Entry<String, Object> entry : this.keyValueMap.entrySet()) {
                Object isValue = isData.keyValueMap.get(entry.getKey());
                if (!entry.getValue().equals(isValue)) {
                    errors.add(new SimpleMetaDataError(Severity.ERROR, inputPort,
                            "pio.error.metadata.general_property_mismatch", new Object[] {
                            entry.getKey(), entry.getValue() }));
                }
            }
        }
        return errors;
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

    /**
     * This will return the meta data description of the given IOObject. If the shortened flag is
     * true, the meta data will be incomplete to avoid to generate too much data if this is
     * supported by the actual meta data implementation.
     */
    public static MetaData forIOObject(IOObject ioo, boolean shortened) {
        return MetaDataFactory.getInstance().createMetaDataforIOObject(ioo, shortened);
    }

    public static MetaData forIOObject(IOObject ioo) {
        return forIOObject(ioo, false);
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotations annotations) {
        this.annotations = annotations;
    }
}
