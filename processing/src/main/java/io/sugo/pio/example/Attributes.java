package io.sugo.pio.example;

import java.io.Serializable;

public interface Attributes extends Iterable<Attribute>, Cloneable, Serializable {
    /** Returns the number of regular attributes. */
    public int size();

    /** Returns the label attribute or null if no label attribute is defined. */
    public Attribute getLabel();
}
