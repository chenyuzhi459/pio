package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Implementation of DataRow that is backed by a double array.
 */
public class DoubleArrayDataRow extends DataRow {

    private static final long serialVersionUID = -6335785895337884919L;

    /**
     * Holds the data for all attributes.
     */
    @JsonUnwrapped
    private double[] data;

    /**
     * Creates a new data row backed by an primitive array.
     */
    public DoubleArrayDataRow(double[] data) {
        this.data = data;
    }

    public DoubleArrayDataRow(DoubleArrayDataRow dataRow) {
        this.data = dataRow.getData();
    }

    public double[] getData() {
        return data;
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
        data[index] = value;
    }

    /**
     * Creates a new array of the given size if necessary and copies the data into the new array.
     */
    @Override
    protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        double[] newData = new double[numberOfColumns];
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
        return DataRowFactory.TYPE_DOUBLE_ARRAY;
    }
}
