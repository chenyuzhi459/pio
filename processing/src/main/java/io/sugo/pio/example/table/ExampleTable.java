package io.sugo.pio.example.table;

import io.sugo.pio.example.Attribute;

import java.io.Serializable;
import java.util.Collection;

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
     * Adds all {@link Attribute}s in <code>newAttributes</code> to the end of the list of
     * attributes, creating new data columns if necessary.
     */
    public void addAttributes(Collection<Attribute> newAttributes);

    /**
     * Adds a clone of the attribute <code>a</code> to the list of attributes assigning it a free
     * column index. The column index is also set on <code>a</code>.
     */
    public int addAttribute(Attribute a);

    /**
     * Equivalent to calling <code>removeAttribute(attribute.getTableIndex())</code>.
     */
    public void removeAttribute(Attribute attribute);

    /**
     * Sets the attribute with the given index to null. Afterwards, this column can be reused.
     * Callers must make sure, that no other example set contains a reference to this column.
     * Otherwise its data will be messed up. Usually this is only possible if an operator generates
     * intermediate attributes, like a validation chain or a feature generator. If the attribute
     * already was removed, this method returns silently.
     */
    public void removeAttribute(int index);

    /**
     * Returns the number of attributes. Attention: Callers that use a for-loop and retrieving
     * {@link Attribute}s by calling {@link AbstractExampleTable#getAttribute(int)} must keep in
     * mind, that some of these attributes may be null.
     */
    public int getNumberOfAttributes();

    /**
     * Returns the number of non null attributes. <b>Attention</b>: Since there might be null
     * attributes in the table, the return value of this method must not be used in a for-loop!
     *
     * @see ExampleTable#getNumberOfAttributes().
     */
    public int getAttributeCount();
}
