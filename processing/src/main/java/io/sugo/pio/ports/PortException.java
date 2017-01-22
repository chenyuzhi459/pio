package io.sugo.pio.ports;

/**
 * @author Simon Fischer
 */
public class PortException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -3144885811799953716L;

    public PortException(String message) {
        super(message);
    }

    public PortException(Port port, String message) {
        super("Exception at " + port.getSpec() + ": " + message);
    }

    public boolean hasRepairOptions() {
        return false;
    }

}