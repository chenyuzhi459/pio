package io.sugo.pio.operator;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.ProcessingStep;
import io.sugo.pio.util.XMLSerialization;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 */
public abstract class AbstractIOObject implements IOObject {
    /** The source of this IOObect. Might be null. */
    private String source = null;

    private transient LinkedList<ProcessingStep> processingHistory = new LinkedList<>();

    private transient HashMap<String, Object> userData = new HashMap<>();

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void appendOperatorToHistory(Operator operator, OutputPort port) {
        if (processingHistory == null) {
            processingHistory = new LinkedList<>();
            if (operator.getProcess() != null) {
                processingHistory.add(new ProcessingStep(operator, port));
            }
        }
        ProcessingStep newStep = new ProcessingStep(operator, port);
        if (operator.getProcess() != null && (processingHistory.isEmpty() || !processingHistory.getLast().equals(newStep))) {
            processingHistory.add(newStep);
        }
    }

    /**
     * Returns not a copy but the very same object.
     */
    @Override
    public IOObject copy() {
        return this;
    }

    /**
     * Initializes the writing of this object. This method is invoked before the actual writing is
     * performed. The default implementation does nothing.
     *
     * This method should also be used for clean up processes which should be performed before the
     * actual writing is done. For example, models might decide to keep the example set information
     * directly after learning (e.g. for visualization reasons) but not to write them down. Please
     * note that all fields will be written into files unless they are set to null in this method or
     * they are marked as transient.
     */
    protected void initWriting() {}

    /**
     * Just serializes this object with help of a {@link XMLSerialization}. Initializes
     * {@link #initWriting()} before the actual writing is performed.
     */
    @Override
    public final void write(OutputStream out) throws IOException {
        initWriting();
        //TODO: Change this to a json serialization
        XMLSerialization.getXMLSerialization().writeXML(this, out);
    }

    @Override
    public Object getUserData(String key) {
        if (userData == null) {
            userData = new HashMap<>();
        }
        return userData.get(key);
    }

    @Override
    public Object setUserData(String key, Object value) {
        return userData.put(key, value);
    }
}
