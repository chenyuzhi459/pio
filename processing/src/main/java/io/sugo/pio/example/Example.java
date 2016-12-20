package io.sugo.pio.example;

import io.sugo.pio.example.table.DataRow;

/**
 */
public class Example {
    /** The data for this example. */
    private DataRow data;

    /** The parent example set holding all attribute information for this data row. */
    private ExampleSet parentExampleSet;

    /**
     * Creates a new Example that uses the data stored in a DataRow. The attributes correspond to
     * the regular and special attributes.
     */
    public Example(DataRow data, ExampleSet parentExampleSet) {
        this.data = data;
        this.parentExampleSet = parentExampleSet;
    }
}
