package io.sugo.pio.operator.io;


import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.OutputPort;


/**
 * Superclass of all operators that have no input and generate a single output. This class is mainly
 * a tribute to the e-LICO DMO.
 *
 * @author Simon Fischer
 */
public abstract class AbstractReader<T extends IOObject> extends Operator {

    private final Class<? extends IOObject> generatedClass;
    private final OutputPort outputPort;

    public AbstractReader(Class<? extends IOObject> generatedClass, OutputPort outputPort) {
        this.outputPort = outputPort;
        this.generatedClass = generatedClass;
    }

    /**
     * Creates (or reads) the ExampleSet that will be returned by {@link #apply()}.
     */
    public abstract T read();

    @Override
    public void doWork() {
        final T result = read();
        outputPort.deliver(result);
    }

}
