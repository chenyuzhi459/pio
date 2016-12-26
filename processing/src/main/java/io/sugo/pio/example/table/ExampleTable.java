package io.sugo.pio.example.table;

import io.sugo.pio.example.Attribute;

import java.io.Serializable;

/**
 */
public interface ExampleTable extends Serializable {

    /** Returns the number of examples. */
    int size();

    /**
     * Returns an Iterator for example data given as <code>DataRow</code> objects. This should be
     * used in all cases where iteration is desired. Since {@link #getDataRow(int)} does not ensure
     * to work in an efficient way the usage of this method is preferred (instead using for-loops).
     */
    DataRowReader getDataRowReader();

    /**
     * Returns the i-th data row. Calling methods cannot rely on the efficiency of this method.
     * Memory based example tables should return the data row in O(1).
     */
    DataRow getDataRow(int index);

    /**
     * Returns the attribute of the column number <i>i</i>. Attention: This value may return null if
     * the column was marked unused.
     */
    public Attribute getAttribute(int i);

    /** Returns a new array containing all {@link Attribute}s. */
    public Attribute[] getAttributes();

    /**
     * Returns the number of attributes. Attention: Callers that use a for-loop and retrieving
     * {@link Attribute}s by calling {@link AbstractExampleTable#getAttribute(int)} must keep in
     * mind, that some of these attributes may be null.
     */
    public int getNumberOfAttributes();
}
