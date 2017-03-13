package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.tools.Tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implementation of DataRow that is backed by a HashMap. Usually using the
 * {@link DoubleSparseArrayDataRow} should be more efficient.
 */
public class SparseMapDataRow extends DataRow {

    private static final long serialVersionUID = -7452459295368606029L;

    /**
     * Maps the indices of attributes to the data.
     */
    @JsonProperty
    private Map<Integer, Double> data = new ConcurrentHashMap<Integer, Double>();

    /**
     * Returns the desired data for the given index.
     */
    @Override
    protected double get(int index, double defaultValue) {
        Double value = data.get(index);
        if (value != null) {
            return value.doubleValue();
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected void set(int index, double value, double defaultValue) {
        if (Tools.isDefault(defaultValue, value)) {
            data.remove(index);
        } else {
            data.put(index, value);
        }
    }

    /**
     * Does nothing.
     */
    @Override
    protected void ensureNumberOfColumns(int numberOfColumns) {
    }

    /**
     * Returns a string representation of the data row.
     */
    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public int getType() {
        return DataRowFactory.TYPE_SPARSE_MAP;
    }
}
