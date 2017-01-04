package io.sugo.pio.example.table;

import io.sugo.pio.example.Attribute;

import java.io.Serializable;

/**
 */
public abstract class DataRow implements Serializable {
    private static final long serialVersionUID = -3482048832637144523L;

    /** Returns the value for the given index. */
    protected abstract double get(int index, double defaultValue);

    /** Sets the given data for the given index. */
    protected abstract void set(int index, double value, double defaultValue);

    /** Sets the value of the {@link Attribute} to <code>value</code>. */
    public void set(Attribute attribute, double value) {
        attribute.setValue(this, value);
    }

    /**
     * Ensures that neither <code>get(i)</code> nor <code>put(i,v)</code> throw a runtime exception
     * for all <i>0 <= i <= numberOfColumns</i>.
     */
    protected abstract void ensureNumberOfColumns(int numberOfColumns);

    /**
     * This returns the type of this particular {@link DataRow} implementation according to the list
     * in the {@link DataRowFactory}.
     */
    public abstract int getType();

    /** Trims the number of columns to the actually needed number. Does nothing by default. */
    public void trim() {}

    /**
     * Returns the value stored at the given {@link Attribute}'s index. Returns Double.NaN if the
     * given attribute is null.
     */
    public double get(Attribute attribute) {
        if (attribute == null) {
            return Double.NaN;
        } else {
            try {
                return attribute.getValue(this);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("DataRow: table index of Attribute " + attribute.getName() + " is out of bounds.");
            }
        }
    }
}
