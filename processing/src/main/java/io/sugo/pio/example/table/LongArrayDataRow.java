package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implementation of DataRow that is backed by a long array.
 */
public class LongArrayDataRow extends DataRow {

    private static final long serialVersionUID = 8652466671294511853L;

    /**
     * Holds the data for all attributes.
     */
    @JsonProperty
    private long[] data;

    /**
     * Creates a new data row backed by an primitive array.
     */
    public LongArrayDataRow(long[] data) {
        this.data = data;
    }

    @Override
    protected double get(int index, double defaultValue) {
        return data[index];
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected synchronized void set(int index, double value, double defaultValue) {
        data[index] = (long) value;
    }

    /**
     * Creates a new array of the given size if necessary and copies the data into the new array.
     */
    @Override
    protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        long[] newData = new long[numberOfColumns];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    /**
     * Returns a string representation of the data row.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            result.append((i == 0 ? "" : ",") + data[i]);
        }
        return result.toString();
    }

    @Override
    public int getType() {
        return DataRowFactory.TYPE_LONG_ARRAY;
    }
}
