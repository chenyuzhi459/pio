package io.sugo.pio.operator;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.table.DataRowReader;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.example.table.MemoryExampleTable;
import io.sugo.pio.operator.preprocessing.MaterializeDataInMemory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.PassThroughRule;
import io.sugo.pio.ports.metadata.SimplePrecondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass of all operators modifying an example set, i.e. accepting an
 * {@link ExampleSet} as input and delivering an {@link ExampleSet} as output. The behavior is
 * delegated from the {@link #doWork()} method to {@link #apply(ExampleSet)}.
 *
 */
public abstract class AbstractExampleSetProcessing extends Operator {

    private static final Logger logger = new Logger(AbstractExampleSetProcessing.class);

    private final InputPort exampleSetInput = getInputPorts().createPort(PortType.EXAMPLE_SET_INPUT);
    private final OutputPort exampleSetOutput = getOutputPorts().createPort(PortType.EXAMPLE_SET_OUTPUT);
    private final OutputPort originalOutput = getOutputPorts().createPort(PortType.ORIGINAL);

    public AbstractExampleSetProcessing() {
        exampleSetInput.addPrecondition(new SimplePrecondition(exampleSetInput, getRequiredMetaData()));
        getTransformer().addRule(new PassThroughRule(exampleSetInput, exampleSetOutput, false) {

            @Override
            public MetaData modifyMetaData(MetaData metaData) {
                if (metaData instanceof ExampleSetMetaData) {
                    try {
                        return AbstractExampleSetProcessing.this.modifyMetaData((ExampleSetMetaData) metaData);
                    } catch (UndefinedParameterError e) {
                        return metaData;
                    }
                } else {
                    return metaData;
                }
            }
        });
        getTransformer().addPassThroughRule(exampleSetInput, originalOutput);
    }

    /**
     * Returns the example set input port, e.g. for adding errors.
     */
    protected final InputPort getInputPort() {
        return exampleSetInput;
    }

    /**
     * Subclasses might override this method to define the meta data transformation performed by
     * this operator.
     *
     * @throws UndefinedParameterError
     */
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) throws UndefinedParameterError {
        return metaData;
    }

    /**
     * Subclasses my override this method to define more precisely the meta data expected by this
     * operator.
     */
    protected ExampleSetMetaData getRequiredMetaData() {
        return new ExampleSetMetaData();
    }

    @Override
    public final void doWork() throws OperatorException {
        ExampleSet inputExampleSet = exampleSetInput.getData(ExampleSet.class);
        ExampleSet applySet = null;

        logger.info("Begin to process example set [%s], size:%d", inputExampleSet.getName(),
                inputExampleSet.size());

        // check for needed copy of original exampleset
        if (writesIntoExistingData()) {
            int type = DataRowFactory.TYPE_DOUBLE_ARRAY;
            if (inputExampleSet.getExampleTable() instanceof MemoryExampleTable) {
                DataRowReader dataRowReader = inputExampleSet.getExampleTable().getDataRowReader();
                if (dataRowReader.hasNext()) {
                    type = dataRowReader.next().getType();
                }
            }
            // check if type is supported to be copied
            if (type >= 0) {
                applySet = MaterializeDataInMemory.materializeExampleSet(inputExampleSet, type);
            }
        }

        if (applySet == null) {
            applySet = (ExampleSet) inputExampleSet.clone();
        }

        // we apply on the materialized data, because writing can't take place in views anyway.
        ExampleSet result = apply(applySet);
        originalOutput.deliver(inputExampleSet);
        exampleSetOutput.deliver(result);
        deliverRemainExampleSet();

        logger.info("Process example set [%s] and deliver to the next operator successfully!",
                inputExampleSet.getName());
    }

    /**
     * Delegate for the apply method. The given ExampleSet is already a clone of the input example
     * set so that changing this examples set does not affect the original one. Subclasses should
     * avoid cloning again unnecessarily.
     */
    public abstract ExampleSet apply(ExampleSet exampleSet) throws OperatorException;

    /**
     * If current operator need to deliver the remained example set to the next operator, then override
     * this method and implement the deliver logistic. Otherwise this method do nothing.
     */
    public void deliverRemainExampleSet() {

    }

    @Override
    public IOContainer getResult() {
        IOContainer container = super.getResult();

        List<IOObject> ioObjects = container.getIoObjects();
//        ioObjects.add(exampleSetInput.getAnyDataOrNull());
        ioObjects.add(exampleSetOutput.getAnyDataOrNull());
//        ioObjects.add(originalOutput.getAnyDataOrNull());

        return container;
    }

    /**
     * This method indicates whether the operator will perform a write operation on a cell in an
     * existing column of the example set's {@link ExampleTable}. If yes, the original example will
     * be completely copied in memory if the original port is used.
     * <p>
     * <strong>Note: </strong> Subclasses must implement this method. The safe implementation would
     * be to return true, however, for backwards compatibility, the default implementation returns
     * false.
     */
    public boolean writesIntoExistingData() {
        return false;
    }

    @Override
    public boolean shouldAutoConnect(OutputPort port) {
        if (port == originalOutput) {
            return getParameterAsBoolean("keep_example_set");
        } else {
            return super.shouldAutoConnect(port);
        }
    }

    public InputPort getExampleSetInputPort() {
        return exampleSetInput;
    }

    public OutputPort getExampleSetOutputPort() {
        return exampleSetOutput;
    }

    /**
     * Used for backward compatibility only.
     *
     * @return
     * @since 7.2.0
     */
    public boolean isOriginalOutputConnected() {
        return originalOutput.isConnected();
    }
}
