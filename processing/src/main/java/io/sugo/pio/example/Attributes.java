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

    /** The name of the confidence special attributes. */
    public static final String CONFIDENCE_NAME = "confidence";

    /** The name of the special attribute label. */
    public static final String LABEL_NAME = "label";

    /** The name of the special attribute id. */
    public static final String ID_NAME = "id";

    /** The name of the special attribute prediction. */
    public static final String PREDICTION_NAME = "prediction";

    /** The name of regular attributes. */
    public static final String ATTRIBUTE_NAME = "attribute";

    /** The name of the special attribute batch. */
    public static final String BATCH_NAME = "batch";

    /** The name of the special attribute outlier. */
    public static final String OUTLIER_NAME = "outlier";

    /** The name of the classification cost special attribute. */
    public static final String CLASSIFICATION_COST = "cost";

    /** The name of the classification cost special attribute. */
    public static final String BASE_VALUE = "base_value";

    /** The name of the special attribute cluster. */
    public static final String CLUSTER_NAME = "cluster";

    /** All known names of regular and special attribute types as an array. */
    public static final String[] KNOWN_ATTRIBUTE_TYPES = new String[] { ATTRIBUTE_NAME, LABEL_NAME, ID_NAME,
            BATCH_NAME, CLUSTER_NAME, PREDICTION_NAME, OUTLIER_NAME, CLASSIFICATION_COST, BASE_VALUE };

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

    /** Removes the given attribute. */
    public boolean remove(Attribute attribute);

    /** Removes the given attribute role. */
    public boolean remove(AttributeRole attributeRole);

    /** Returns the attribute role for the given attribute. */
    public AttributeRole getRole(Attribute attribute);

    /** Returns the attribute role for the given name. */
    public AttributeRole getRole(String name);

    /** Returns the special attribute for the given special name. */
    public Attribute getSpecial(String name);

    /** Returns the label attribute or null if no label attribute is defined. */
    public Attribute getLabel();

    /** Finds the {@link AttributeRole} with the given special name (both regular and special). */
    public AttributeRole findRoleBySpecialName(String specialName);

    /**
     * Finds the {@link AttributeRole} belonging to the attribute with the given name (both regular
     * and special). Search is performed case sensitive.
     */
    public AttributeRole findRoleByName(String name);

    /**
     * Finds the {@link AttributeRole} belonging to the attribute with the given name (both regular
     * and special). If the search is performed case sensitive depends on the boolean parameter.
     * Attention: Case insensitive search is not optimized and takes linear time with number of
     * attributes.
     */
    public AttributeRole findRoleByName(String name, boolean caseSensitive);

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
