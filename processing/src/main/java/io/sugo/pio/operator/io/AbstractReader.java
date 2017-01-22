package io.sugo.pio.operator.io;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.MetaDataError;
import io.sugo.pio.ports.metadata.SimpleMetaDataError;
import io.sugo.pio.tools.io.Encoding;
import io.sugo.pio.operator.ProcessSetupError.Severity;

import java.util.List;


/**
 * Superclass of all operators that have no input and generate a single output. This class is mainly
 * a tribute to the e-LICO DMO.
 *
 */
public abstract class AbstractReader<T extends IOObject> extends Operator {

    private final OutputPort outputPort = getOutputPorts().createPort("output");
    private final Class<? extends IOObject> generatedClass;

    private boolean cacheDirty = true;
    private MetaData cachedMetaData;
    private MetaDataError cachedError;

    public AbstractReader(Class<? extends IOObject> generatedClass) {
        this.generatedClass = generatedClass;
        getTransformer().addRule(new MDTransformationRule() {

            @Override
            public void transformMD() {
                if (cacheDirty || !isMetaDataCacheable()) {
                    try {
                        // TODO add extra thread for meta data generation?
                        cachedMetaData = AbstractReader.this.getGeneratedMetaData();
                        cachedError = null;
                    } catch (OperatorException e) {
                        cachedMetaData = new MetaData(AbstractReader.this.generatedClass);
                        String msg = e.getMessage();
                        if ((msg == null) || (msg.length() == 0)) {
                            msg = e.toString();
                        }
                        // will be added below
                        cachedError = new SimpleMetaDataError(Severity.WARNING, outputPort,
                                "cannot_create_exampleset_metadata", new Object[] { msg });
                    }
                    if (cachedMetaData != null) {
                        cachedMetaData.addToHistory(outputPort);
                    }
                    cacheDirty = false;
                }
                outputPort.deliverMD(cachedMetaData);
                if (cachedError != null) {
                    outputPort.addError(cachedError);
                }
            }
        });
    }

    public MetaData getGeneratedMetaData() throws OperatorException {
        return new MetaData(generatedClass);
    }

    protected boolean isMetaDataCacheable() {
        return false;
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

    @Override
    protected void registerOperator(OperatorProcess process) {
        super.registerOperator(process);
        cacheDirty = true;
    }

    protected boolean supportsEncoding() {
        return false;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        if (supportsEncoding()) {
            types.addAll(Encoding.getParameterTypes(this));
        }
        return types;
    }

}
