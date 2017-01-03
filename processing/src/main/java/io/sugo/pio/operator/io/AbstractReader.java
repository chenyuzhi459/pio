package io.sugo.pio.operator.io;


import io.sugo.pio.Process;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.impl.OutputPortImpl;
import io.sugo.pio.ports.metadata.MetaData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Superclass of all operators that have no input and generate a single output. This class is mainly
 * a tribute to the e-LICO DMO.
 *
 * @author Simon Fischer
 */
public abstract class AbstractReader<T extends IOObject> extends Operator {

    //	private final OutputPort outputPort = getOutputPorts().createPort("output");
//	private final OutputPort outputPort = new OutputPortImpl("output");
    private final OutputPort outputPort;
    private boolean cacheDirty = true;
    private MetaData cachedMetaData;

    public AbstractReader(String name, OutputPort outputPort) {
        super(name, null, Arrays.asList(outputPort));
        this.outputPort = outputPort;
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


    /**
     * Describes an operator that can read certain file types.
     */
    public static class ReaderDescription {

        private final String fileExtension;
        private final Class<? extends AbstractReader> readerClass;
        /**
         * This parameter must be set to the file name.
         */
        private final String fileParameterKey;

        public ReaderDescription(String fileExtension, Class<? extends AbstractReader> readerClass, String fileParameterKey) {
            super();
            this.fileExtension = fileExtension;
            this.readerClass = readerClass;
            this.fileParameterKey = fileParameterKey;
        }
    }

    private static final Map<String, ReaderDescription> READER_DESCRIPTIONS = new HashMap<String, ReaderDescription>();

    /**
     * Registers an operator that can read files with a given extension.
     */
    protected static void registerReaderDescription(ReaderDescription rd) {
        READER_DESCRIPTIONS.put(rd.fileExtension.toLowerCase(), rd);
    }

    @Override
    protected void registerOperator(Process process) {
        super.registerOperator(process);
        cacheDirty = true;
    }
}
