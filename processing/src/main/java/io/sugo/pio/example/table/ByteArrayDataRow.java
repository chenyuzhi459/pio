package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Implementation of DataRow that is backed by a byte array. Please note that using this data row is
 * quite efficient but only supports 256 different values (integers between -127 and 128 or 256
 * different nominal values for each column).
 */
public class ByteArrayDataRow extends DataRow {

    private static final long serialVersionUID = -1428468572995891360L;

    /**
     * Holds the data for all attributes.
     */
    @JsonProperty
    private byte[] data;

    /**
     * Creates a new data row backed by an primitive array.
     */
    public ByteArrayDataRow(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the value for the desired index. This method should only be used by attributes.
     */
    @Override
    protected double get(int index, double defaultValue) {
        return data[index];
    }

    /**
     * Sets the value for the given index. This method should only be used by attributes.
     */
    @Override
    protected synchronized void set(int index, double value, double defaultValue) {
        data[index] = (byte) value;
    }

    /**
     * Creates a new array of the given size if necessary and copies the data into the new array.
     */
    @Override
    protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        byte[] newData = new byte[numberOfColumns];
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
        return DataRowFactory.TYPE_BYTE_ARRAY;
    }
}
