package io.sugo.pio.example.set;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.ExampleTable;

import java.util.Iterator;

/**
 */
public class SplittedExampleSet extends AbstractExampleSet {
    /** The partition. */
    private Partition partition;

    /** The parent example set. */
    private ExampleSet parent;

    /** Constructs a SplittedExampleSet with the given partition. */
    public SplittedExampleSet(ExampleSet exampleSet, Partition partition) {
        this.parent = (ExampleSet) exampleSet.clone();
        this.partition = partition;
    }

    @Override
    public Attributes getAttributes() {
        return parent.getAttributes();
    }

    @Override
    public int size() {
        return partition.getSelectionSize();
    }

    @Override
    public ExampleTable getExampleTable() {
        return parent.getExampleTable();
    }

    /**
     * Searches i-th example in the currently selected partition. This is done in constant time.
     */
    @Override
    public Example getExample(int index) {
        int actualIndex = partition.mapIndex(index);
        return parent.getExample(actualIndex);
    }

    /** Returns an example reader that splits all examples that are not selected. */
    @Override
    public Iterator<Example> iterator() {
        return new IndexBasedExampleSetReader(this);
    }
}
