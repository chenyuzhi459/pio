package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.example.Attribute;

import java.io.Serializable;

/**
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "data_row")
/*@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "byte_data_row", value = ByteArrayDataRow.class),
        @JsonSubTypes.Type(name = "byte_sparse_data_row", value = ByteSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "bool_data_row", value = BooleanArrayDataRow.class),
        @JsonSubTypes.Type(name = "bool_sparse_data_row", value = BooleanSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "double_data_row", value = DoubleArrayDataRow.class),
        @JsonSubTypes.Type(name = "double_sparse_data_row", value = DoubleSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "float_data_row", value = FloatArrayDataRow.class),
        @JsonSubTypes.Type(name = "float_sparse_data_row", value = FloatSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "int_data_row", value = IntArrayDataRow.class),
        @JsonSubTypes.Type(name = "int_sparse_data_row", value = IntSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "long_data_row", value = LongArrayDataRow.class),
        @JsonSubTypes.Type(name = "long_sparse_data_row", value = LongSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "short_data_row", value = ShortArrayDataRow.class),
        @JsonSubTypes.Type(name = "short_sparse_data_row", value = ShortSparseArrayDataRow.class),
        @JsonSubTypes.Type(name = "map_sparse_data_row", value = SparseMapDataRow.class)
})*/
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
