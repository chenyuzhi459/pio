package io.sugo.pio.example.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.*;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This is a possible abstract superclass for all attribute implementations. Most methods of
 * {@link Attribute} are already implemented here.
 */

public abstract class AbstractAttribute implements Attribute {

    private static final long serialVersionUID = -9167755945651618227L;

    private transient List<Attributes> owners = new LinkedList<Attributes>();

    /** The basic information about the attribute. Will only be shallowly cloned. */
    @JsonProperty
    private AttributeDescription attributeDescription;

    private final List<AttributeTransformation> transformations = new ArrayList<AttributeTransformation>();

    /**
     * Contains all attribute statistics calculation algorithms.
     */
    private List<Statistics> statistics = new LinkedList<Statistics>();

    /**
     * The current attribute construction description object.
     */
    private String constructionDescription = null;

    private Annotations annotations = new Annotations();

//    private int tableIndex = Attribute.UNDEFINED_ATTRIBUTE_INDEX;
    private int valueType;
//    private double defaultValue = 0.0;
//    private int blockType = Ontology.SINGLE_VALUE;

    // --------------------------------------------------------------------------------

    /**
     * Creates a simple attribute which is not part of a series and does not provide a unit string.
     * This constructor should only be used for attributes which were not generated with help of a
     * generator, i.e. this attribute has no function arguments. Only the last transformation is
     * cloned, the other transformations are cloned by reference.
     */
    protected AbstractAttribute(AbstractAttribute attribute) {
//        tableIndex = attribute.tableIndex;
        this.attributeDescription = attribute.attributeDescription;

        // copy statistics
        this.statistics = new LinkedList<Statistics>();
        for (Statistics statistics : attribute.statistics) {
            this.statistics.add((Statistics) statistics.clone());
        }

        // copy transformations if necessary (only the transformation on top of the view stack!)
        int counter = 0;
        for (AttributeTransformation transformation : attribute.transformations) {
            if (counter < attribute.transformations.size() - 1) {
                addTransformation(transformation);
            } else {
                addTransformation((AttributeTransformation) transformation.clone());
            }
            counter++;
        }

        // copy construction description
        this.constructionDescription = attribute.constructionDescription;

        // copy annotations
        annotations.putAll(attribute.getAnnotations());
    }

    /**
     * Creates a simple attribute which is not part of a series and does not provide a unit string.
     * This constructor should only be used for attributes which were not generated with help of a
     * generator, i.e. this attribute has no function arguments.
     */
    protected AbstractAttribute(String name, int valueType) {
        this.attributeDescription = new AttributeDescription(this, name, valueType, Ontology.SINGLE_VALUE, 0.0d,
                UNDEFINED_ATTRIBUTE_INDEX);
        this.constructionDescription = name;
//        this.valueType = valueType;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (owners == null) {
            owners = new LinkedList<Attributes>();
        }
        if (annotations == null) {
            annotations = new Annotations();
        }
    }

    @Override
    public void addOwner(Attributes attributes) {
        this.owners.add(attributes);
    }

    @Override
    public void removeOwner(Attributes attributes) {
        this.owners.remove(attributes);
    }

    /**
     * Clones this attribute.
     */
    @Override
    public abstract Object clone();

    /**
     * Returns true if the given attribute has the same name and the same table index.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractAttribute)) {
            return false;
        }
        AbstractAttribute a = (AbstractAttribute) o;
        return this.attributeDescription.equals(a.attributeDescription);
    }

    @Override
    public int hashCode() {
        return attributeDescription.hashCode();
    }

    @Override
    public void addTransformation(AttributeTransformation transformation) {
        transformations.add(transformation);
    }

    @Override
    public void clearTransformations() {
        this.transformations.clear();
    }

    @Override
    public AttributeTransformation getLastTransformation() {
        int size = this.transformations.size();
        if (size > 0) {
            return this.transformations.get(size - 1);
        } else {
            return null;
        }
    }

    @Override
    public double getValue(DataRow row) {
        double result = row.get(getTableIndex(), getDefault());
        if (!transformations.isEmpty()) {
            for (AttributeTransformation transformation : transformations) {
                result = transformation.transform(this, result);
            }
        }
        return result;
    }

    @Override
    public void setValue(DataRow row, double value) {
        double newValue = value;
        for (AttributeTransformation transformation : transformations) {
            if (transformation.isReversable()) {
                newValue = transformation.inverseTransform(this, newValue);
            } else {
                throw new RuntimeException(
                        "Cannot set value for attribute using irreversible transformations. This process will probably work if you deactivate create_view in preprocessing operators.");
            }
        }
        row.set(getTableIndex(), newValue, getDefault());
    }

    /**
     * Returns the name of the attribute.
     */
    @Override
    public String getName() {
        return this.attributeDescription.getName();
    }

    /**
     * Sets the name of the attribute.
     */
    @Override
    public void setName(String v) {
        if (v.equals(this.attributeDescription.getName())) {
            return;
        }
        for (Attributes attributes : owners) {
            attributes.rename(this, v);
        }
        this.attributeDescription = (AttributeDescription) this.attributeDescription.clone();
        this.attributeDescription.setName(v);
    }

    /**
     * Returns the index in the example table.
     */
    @Override
    public int getTableIndex() {
        return this.attributeDescription.getTableIndex();
    }

    /**
     * Sets the index in the example table.
     */
    @Override
    public void setTableIndex(int i) {
        this.attributeDescription = (AttributeDescription) this.attributeDescription.clone();
        this.attributeDescription.setTableIndex(i);
    }

    @Override
    public int getBlockType() {
        return this.attributeDescription.getBlockType();
    }

    /**
     * Sets the block type of this attribute.
     *
     * @see io.sugo.pio.tools.Ontology#ATTRIBUTE_BLOCK_TYPE
     */
    @Override
    public void setBlockType(int b) {
        this.attributeDescription = (AttributeDescription) this.attributeDescription.clone();
        this.attributeDescription.setBlockType(b);
    }

    /**
     * Returns the value type of this attribute.
     *
     * @see io.sugo.pio.tools.Ontology#ATTRIBUTE_VALUE_TYPE
     */
    @Override
    public int getValueType() {
        return this.attributeDescription.getValueType();
    }

    /**
     * Returns the attribute statistics.
     */
    @Override
    public Iterator<Statistics> getAllStatistics() {
        return statistics.iterator();
    }

    @Override
    public void registerStatistics(Statistics statistics) {
        this.statistics.add(statistics);
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    /** Returns the construction description. */
    @Override
    public String getConstruction() {
        return this.constructionDescription;
    }

    /** Returns the construction description. */
    @Override
    public void setConstruction(String description) {
        this.constructionDescription = description;
    }

    @Override
    public void setDefault(double value) {
        this.attributeDescription = (AttributeDescription) this.attributeDescription.clone();
        this.attributeDescription.setDefault(value);
    }

    @Override
    public double getDefault() {
        return this.attributeDescription.getDefault();
    }

    /** Returns a human readable string that describes this attribute. */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("#");
        result.append(this.attributeDescription.getTableIndex());
        result.append(": ");
        result.append(this.attributeDescription.getName());
        result.append(" (");
        result.append(Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(this.attributeDescription.getValueType()));
        result.append("/");
        result.append(Ontology.ATTRIBUTE_BLOCK_TYPE.mapIndex(this.attributeDescription.getBlockType()));
        result.append(")");
        return result.toString();
    }
}
