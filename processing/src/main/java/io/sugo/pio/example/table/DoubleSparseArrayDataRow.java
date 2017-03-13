package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implementation of DataRow that is backed by primitive arrays. Should always be used if more than
 * 50% of the data is sparse. As fast (or even faster than map implementation) but needs
 * considerably less memory.
 */
public class DoubleSparseArrayDataRow extends AbstractSparseArrayDataRow {

    private static final long serialVersionUID = 9137639592169696234L;

    /**
     * Stores the used attribute values.
     */
    @JsonProperty
    private double[] values;

    /**
     * Creates an empty sparse array data row with size 0.
     */
    public DoubleSparseArrayDataRow() {
        this(0);
    }

    /**
     * Creates a sparse array data row of the given size.
     */
    public DoubleSparseArrayDataRow(int size) {
        super(size);
        values = new double[size];
    }

    @Override
    protected void swapValues(int a, int b) {
        double tt = values[a];
        values[a] = values[b];
        values[b] = tt;
    }

    @Override
    protected void resizeValues(int length) {
        double[] d = new double[length];
        System.arraycopy(values, 0, d, 0, Math.min(values.length, length));
        values = d;
    }

    @Override
    protected void removeValue(int index) {
        System.arraycopy(values, index + 1, values, index, values.length - (index + 1));
    }

    /**
     * Returns the desired data for the given attribute.
     */
    @Override
    protected double getValue(int index) {
        return values[index];
    }

    /**
     * Sets the given data for the given attribute.
     */
    @Override
    protected void setValue(int index, double v) {
        values[index] = v;
    }

    @Override
    protected double[] getAllValues() {
        return this.values;
    }

    @Override
    public int getType() {
        return DataRowFactory.TYPE_DOUBLE_SPARSE_ARRAY;
    }
}
