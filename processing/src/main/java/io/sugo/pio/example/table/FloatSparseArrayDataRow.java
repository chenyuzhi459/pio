package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Implementation of DataRow that is backed by primitive arrays. Should always be used if more than
 * 50% of the data is sparse. As fast (or even faster than map implementation) but needs
 * considerably less memory. This implementation uses float arrays instead of double arrays which
 * might reduce the used memory even more.
 */
public class FloatSparseArrayDataRow extends AbstractSparseArrayDataRow {

    private static final long serialVersionUID = -2445500346242180129L;

    /**
     * Stores the used attribute values.
     */
    private float[] values;

    /**
     * Creates an empty sparse array data row with size 0.
     */
    public FloatSparseArrayDataRow() {
        this(0);
    }

    /**
     * Creates a sparse array data row of the given size.
     */
    public FloatSparseArrayDataRow(int size) {
        super(size);
        values = new float[size];
    }

    @JsonValue
    public float[] getValues() {
        return values;
    }

    @Override
    protected void swapValues(int a, int b) {
        float tt = values[a];
        values[a] = values[b];
        values[b] = tt;
    }

    @Override
    protected void resizeValues(int length) {
        float[] d = new float[length];
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
        values[index] = (float) v;
    }

    @Override
    protected double[] getAllValues() {
        double[] result = new double[this.values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.values[i];
        }
        return result;
    }

    @Override
    public int getType() {
        return DataRowFactory.TYPE_FLOAT_SPARSE_ARRAY;
    }
}
