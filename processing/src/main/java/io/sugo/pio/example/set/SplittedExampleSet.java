package io.sugo.pio.example.set;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.tools.Tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    /** Selects exactly one subset. */
    public void selectSingleSubset(int index) {
        partition.clearSelection();
        partition.selectSubset(index);
    }


    /** Returns the number of subsets. */
    public int getNumberOfSubsets() {
        return partition.getNumberOfSubsets();
    }

    /** Returns an example reader that splits all examples that are not selected. */
    @Override
    public Iterator<Example> iterator() {
        return new IndexBasedExampleSetReader(this);
    }

    // -------------------- Factory methods --------------------

    /**
     * Works only for nominal and integer attributes. If <i>k</i> is the number of different values,
     * this method splits the example set into <i>k</i> subsets according to the value of the given
     * attribute.
     */
    public static SplittedExampleSet splitByAttribute(ExampleSet exampleSet, Attribute attribute) {
        int[] elements = new int[exampleSet.size()];
        int i = 0;
        Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
        AtomicInteger currentIndex = new AtomicInteger(0);
        for (Example example : exampleSet) {
            int value = (int) example.getValue(attribute);
            Integer indexObject = indexMap.get(value);
            if (indexObject == null) {
                indexMap.put(value, currentIndex.getAndIncrement());
            }
            int intValue = indexMap.get(value).intValue();
            elements[i++] = intValue;
        }

        int maxNumber = indexMap.size();
        indexMap.clear();
        Partition partition = new Partition(elements, maxNumber);
        return new SplittedExampleSet(exampleSet, partition);
    }

    /**
     * Works only for real-value attributes. Returns an example set splitted into two parts
     * containing all examples providing a greater (smaller) value for the given attribute than the
     * given value. The first partition contains all examples providing a smaller or the same value
     * than the given one.
     */
    public static SplittedExampleSet splitByAttribute(ExampleSet exampleSet, Attribute attribute, double value) {
        int[] elements = new int[exampleSet.size()];
        Iterator<Example> reader = exampleSet.iterator();
        int i = 0;
        while (reader.hasNext()) {
            Example example = reader.next();
            double currentValue = example.getValue(attribute);
            if (Tools.isLessEqual(currentValue, value)) {
                elements[i++] = 0;
            } else {
                elements[i++] = 1;
            }
        }
        Partition partition = new Partition(elements, 2);
        return new SplittedExampleSet(exampleSet, partition);
    }

}
