package io.sugo.pio.example;

import java.io.Serializable;
import java.util.Iterator;

public interface Attributes extends Iterable<Attribute>, Cloneable, Serializable {
    /** Indicates regular attributes. */
    public static final int REGULAR = 0;

    /** Indicates special attributes. */
    public static final int SPECIAL = 1;

    /** Indicates all attributes. */
    public static final int ALL = 2;

    /** The name of the special attribute label. */
    public static final String LABEL_NAME = "label";

    /** The name of regular attributes. */
    public static final String ATTRIBUTE_NAME = "attribute";

    /** Returns a clone of this attribute set. */
    public Object clone();

    /** Returns an iterator over all attributes, including the special attributes. */
    public Iterator<Attribute> allAttributes();

    /** Returns an iterator over all attribute roles, including the special attribute roles. */
    public Iterator<AttributeRole> allAttributeRoles();

    /** Returns the number of regular attributes. */
    public int size();

    /** Returns the number of all attributes, i.e. of the regular and the special attributes. */
    public int allSize();

    /** Adds a new attribute role. */
    public void add(AttributeRole attributeRole);

    /** Returns the special attribute for the given special name. */
    public Attribute getSpecial(String name);

    /** Returns the label attribute or null if no label attribute is defined. */
    public Attribute getLabel();

    /** Finds the {@link AttributeRole} with the given special name (both regular and special). */
    public AttributeRole findRoleBySpecialName(String specialName);

    /**
     * Finds the {@link AttributeRole} with the given special name (both regular and special). If
     * the search is performed case sensitive depends on the boolean parameter. Attention: Case
     * insensitive search is not optimized and takes linear time with number of attributes.
     */
    public AttributeRole findRoleBySpecialName(String specialName, boolean caseSensitive);

    /** @see #rename(Attribute, String) */
    public void rename(AttributeRole attributeRole, String newSpecialName);

    /**
     * Notifies the Attributes that this attribute will rename itself to the given name immediately
     * after this method returns.
     */
    public void rename(Attribute attribute, String newName);

}
