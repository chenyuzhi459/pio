package io.sugo.pio.example;

import io.sugo.pio.example.table.NominalMapping;

import java.io.Serializable;

/**
 */
public interface Attribute extends Cloneable, Serializable {
    /** Sets the construction description. */
    public void setConstruction(String description);

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an
     * {@link UnsupportedOperationException} for non-nominal attributes.
     */
    public NominalMapping getMapping();

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an exception for non-nominal
     * attributes.
     */
    public void setMapping(NominalMapping nominalMapping);
}
