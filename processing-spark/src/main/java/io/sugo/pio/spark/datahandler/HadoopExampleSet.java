package io.sugo.pio.spark.datahandler;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.set.AbstractExampleSet;
import io.sugo.pio.example.set.SimpleExampleReader;
import io.sugo.pio.example.table.ExampleTable;

import java.util.Iterator;

/**
 */
public class HadoopExampleSet extends AbstractExampleSet {
    private ExampleTable exampleTable;

    public HadoopExampleSet(ExampleTable exampleTable) {
        this.exampleTable = exampleTable;
    }

    @Override
    public Iterator<Example> iterator() {
        return new SimpleExampleReader(exampleTable.getDataRowReader(), this);
    }
}
