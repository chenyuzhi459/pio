package io.sugo.pio.example;

import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.operator.ResultObject;

/**
 */
public interface ExampleSet extends ResultObject, Cloneable, Iterable<Example> {
    /**
     * Returns the data structure holding all attributes. NOTE! if you intend to iterate over all
     * Attributes of this ExampleSet then you need to create an Iterator by calling
     * {@link ExampleSet#getAttributes()#getAttributes()} and use it instead.
     */
    Attributes getAttributes();

    /**
     * Returns the number of examples in this example set. This number should not be used to create
     * for-loops to iterate through all examples.
     */
    int size();

    /**
     * Returns the underlying example table. Most operators should operate on the example set and
     * manipulate example to change table data instead of using the table directly.
     */
    ExampleTable getExampleTable();

    /** Clones the example set. */
    public Object clone();
}
