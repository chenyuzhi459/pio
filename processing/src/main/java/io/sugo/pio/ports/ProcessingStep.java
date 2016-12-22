package io.sugo.pio.ports;

import io.sugo.pio.operator.Operator;

/**
 * This class holds information about a processing step for an IOObject. Currently the name of the
 * operator and port is remembered.
 *
 * @author Sebastian Land
 */
public class ProcessingStep {

    private String operatorName;

    private String portName;

    /**
     * This constructor builds a ProcessingStep from the current operator and the used outputport of
     * the respective IOObject.
     */
    public ProcessingStep(Operator operator, OutputPort port) {
        this.operatorName = operator.getName();
        this.portName = port.getName();
    }

    /**
     * This returns the name of the operator, which processed the respective IOObject.
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * This return the name of the output port, which was used to return the respective IOObject.
     */
    public String getPortName() {
        return portName;
    }

    @Override
    public int hashCode() {
        return operatorName.hashCode() ^ portName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProcessingStep)) {
            return false;
        }
        ProcessingStep other = (ProcessingStep) obj;
        return operatorName.equals(other.operatorName) && portName.equals(other.portName);
    }

    @Override
    public String toString() {
        return operatorName + "." + portName;
    }
}
