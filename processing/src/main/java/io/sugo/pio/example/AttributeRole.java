package io.sugo.pio.example;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class AttributeRole implements Serializable {
    private boolean special = false;

    private String specialName = null;

    private Attribute attribute;

    private transient List<Attributes> owners = new LinkedList<Attributes>();

    public AttributeRole(Attribute attribute) {
        this.attribute = attribute;
    }

    /** Clone constructor. */
    private AttributeRole(AttributeRole other) {
        this.attribute = (Attribute) other.attribute.clone();
        this.special = other.special;
        this.specialName = other.specialName;
    }


    /** Performs a deep clone of the special fields but only a shallow clone of the attribute. */
    @Override
    public Object clone() {
        return new AttributeRole(this);
    }

    /**
     * This method must not be called except by the {@link Attributes} to which this AttributeRole
     * is added.
     */
    protected void addOwner(Attributes attributes) {
        this.owners.add(attributes);
    }

    /**
     * This method must not be called except by the {@link Attributes} to which this AttributeRole
     * is added.
     */
    protected void removeOwner(Attributes attributes) {
        this.owners.remove(attributes);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public boolean isSpecial() {
        return special;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecial(String specialName) {
        for (Attributes attributes : owners) {
            attributes.rename(this, specialName);
        }
        this.specialName = specialName;
        if (specialName != null) {
            this.special = true;
        } else {
            this.special = false;
        }
    }

}
