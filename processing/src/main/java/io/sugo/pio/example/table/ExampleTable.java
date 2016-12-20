package io.sugo.pio.example.table;

import java.io.Serializable;

/**
 */
public interface ExampleTable extends Serializable {
    /**
     * Returns an Iterator for example data given as <code>DataRow</code> objects. This should be
     * used in all cases where iteration is desired. Since {@link #getDataRow(int)} does not ensure
     * to work in an efficient way the usage of this method is preferred (instead using for-loops).
     */
    public DataRowReader getDataRowReader();

    /**
     * Returns the i-th data row. Calling methods cannot rely on the efficiency of this method.
     * Memory based example tables should return the data row in O(1).
     */
    public DataRow getDataRow(int index);
}
