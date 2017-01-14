package io.sugo.pio.spark.datahandler;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.SimpleAttributes;
import io.sugo.pio.example.set.AbstractExampleSet;
import io.sugo.pio.example.set.SimpleExampleReader;
import io.sugo.pio.example.table.ExampleTable;

import java.util.Iterator;

/**
 */
public class HadoopExampleSet extends AbstractExampleSet {
    private ExampleTable exampleTable;
    public Attributes attributes;

    public HadoopExampleSet(ExampleTable exampleTable) {
        this.exampleTable = exampleTable;
        this.attributes = new SimpleAttributes();
    }

    @Override
    public Iterator<Example> iterator() {
        return new SimpleExampleReader(exampleTable.getDataRowReader(), this);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public int size() {
        return exampleTable.size();
    }

    @Override
    public ExampleTable getExampleTable() {
        return exampleTable;
    }

    @Override
    public Example getExample(int index) {
        return null;
    }
}
