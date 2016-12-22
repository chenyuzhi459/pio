package io.sugo.pio.example;

import io.sugo.pio.example.table.NominalMapping;

import java.io.Serializable;

/**
 */
public interface Attribute extends Cloneable, Serializable {

    /** Clones this attribute. */
    public Object clone();

    /** Returns the name of the attribute. */
    public String getName();

    /** Sets the name of the attribute. */
    public void setName(String name);

    /**
     * Sets the Attributes instance to which this attribute belongs. This instance will be notified
     * when the attribute renames itself. This method must not be called except by the
     * {@link Attributes} to which this AttributeRole is added.
     */
    public void addOwner(Attributes attributes);

    public void removeOwner(Attributes attributes);

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
