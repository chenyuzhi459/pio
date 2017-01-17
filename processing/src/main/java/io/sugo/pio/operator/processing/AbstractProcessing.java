package io.sugo.pio.operator.processing;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.table.DataRowReader;
import io.sugo.pio.example.table.MemoryExampleTable;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * Created by root on 17-1-13.
 */
public abstract class AbstractProcessing extends Operator {
    private final InputPort exampleSetInput = getInputPorts().createPort("example set input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    protected final InputPort getInputPort() {
        return exampleSetInput;
    }

    @Override
    public final void doWork() {
        ExampleSet inputExampleSet = exampleSetInput.getData(ExampleSet.class);
        ExampleSet applySet = null;
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
//            if (type >= 0) {
//                applySet = MaterializeDataInMemory.materializeExampleSet(inputExampleSet, type);
//            }
        }

        if (applySet == null) {
            applySet = (ExampleSet) inputExampleSet.clone();
        }

        // we apply on the materialized data, because writing can't take place in views anyway.
        ExampleSet result = apply(applySet);
        originalOutput.deliver(inputExampleSet);
        exampleSetOutput.deliver(result);
    }

    public abstract ExampleSet apply(ExampleSet exampleSet);

    public boolean writesIntoExistingData() {
        return false;
    }

    public InputPort getExampleSetInputPort() {
        return exampleSetInput;
    }

    public OutputPort getExampleSetOutputPort() {
        return exampleSetOutput;
    }

    public boolean isOriginalOutputConnected() {
        return originalOutput.isConnected();
    }
}
