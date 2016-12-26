package io.sugo.pio.example;

import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.tools.Ontology;

import java.io.Serializable;

/**
 */
public interface Attribute extends Cloneable, Serializable {

    /**
     * Clones this attribute.
     */
    Object clone();

    /**
     * Returns the name of the attribute.
     */
    String getName();

    /**
     * Sets the name of the attribute.
     */
    void setName(String name);

    /**
     * Sets the Attributes instance to which this attribute belongs. This instance will be notified
     * when the attribute renames itself. This method must not be called except by the
     * {@link Attributes} to which this AttributeRole is added.
     */
    void addOwner(Attributes attributes);

    void removeOwner(Attributes attributes);

    /** Returns the index in the example table. */
    public int getTableIndex();

    /** Sets the index in the example table. */
    public void setTableIndex(int index);

    /**
     * Sets the construction description.
     */
    void setConstruction(String description);

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an
     * {@link UnsupportedOperationException} for non-nominal attributes.
     */
    NominalMapping getMapping();

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an exception for non-nominal
     * attributes.
     */
    void setMapping(NominalMapping nominalMapping);

    /**
     * Returns the value for the column this attribute corresponds to in the given data row.
     */
    double getValue(DataRow row);

    /**
     * Sets the value for the column this attribute corresponds to in the given data row.
     */
    void setValue(DataRow row, double value);

    /**
     * Returns true if the attribute is nominal.
     */
    boolean isNominal();

    /**
     * Returns true if the attribute is numerical.
     */
    boolean isNumerical();

    /**
     * Returns true if the attribute is date_time.
     */
    boolean isDateTime();

    /**
     * Returns the value type of this attribute.
     *
     * @see Ontology#ATTRIBUTE_VALUE_TYPE
     */
    int getValueType();

    /**
     * Returns a formatted string of the given value according to the attribute type.
     */
    String getAsString(double value, int digits, boolean quoteNominal);
}
