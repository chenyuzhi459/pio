package io.sugo.pio.operator.io;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MetaData;

/**
 * Superclass of all operators that have no input and generate a single output. This class is mainly
 * a tribute to the e-LICO DMO.
 *
 * @author Simon Fischer
 */
public abstract class AbstractReader<T extends IOObject> extends Operator {
    private final OutputPort outputPort = getOutputPorts().createPort("output");
    private final Class<? extends IOObject> generatedClass;

    public AbstractReader(OperatorDescription description, Class<? extends IOObject> generatedClass) {
        super(description);
        this.generatedClass = generatedClass;
        getTransformer().addRule(new MDTransformationRule() {
            @Override
            public void transformMD() {
                MetaData metaData;
                try {
                    metaData = getGeneratedMetaData();
                } catch (Exception e) {
                    metaData = new MetaData(generatedClass);
                }
                outputPort.deliverMD(metaData);
            }
        });
    }

    public MetaData getGeneratedMetaData() {
        return new MetaData(generatedClass);
    }

    /** Creates (or reads) the ExampleSet that will be returned by {@link #apply()}. */
    public abstract T read();

    @Override
    public void doWork() {
        final T result = read();
        outputPort.deliver(result);
    }
}
