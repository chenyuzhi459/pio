package io.sugo.pio.example.table;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeTransformation;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Statistics;
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

    private int tableIndex = Attribute.UNDEFINED_ATTRIBUTE_INDEX;
    private int valueType;
    private double defaultValue = 0.0;

    // --------------------------------------------------------------------------------

    /**
     * Creates a simple attribute which is not part of a series and does not provide a unit string.
     * This constructor should only be used for attributes which were not generated with help of a
     * generator, i.e. this attribute has no function arguments. Only the last transformation is
     * cloned, the other transformations are cloned by reference.
     */
    protected AbstractAttribute(AbstractAttribute attribute) {
        tableIndex = attribute.tableIndex;
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
        this.constructionDescription = name;
        this.valueType = valueType;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (owners == null) {
            owners = new LinkedList<Attributes>();
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
        return this.constructionDescription.equals(a.constructionDescription);
    }

    @Override
    public int hashCode() {
        return constructionDescription.hashCode();
    }

    /**
     * Returns the name of the attribute.
     */
    @Override
    public String getName() {
        return this.constructionDescription;
    }

    /**
     * Sets the name of the attribute.
     */
    @Override
    public void setName(String v) {
    }

    /**
     * Returns the index in the example table.
     */
    @Override
    public int getTableIndex() {
        return tableIndex;
    }

    /**
     * Sets the index in the example table.
     */
    @Override
    public void setTableIndex(int i) {
        tableIndex = i;
    }

    /**
     * Returns the construction description.
     */
    @Override
    public void setConstruction(String description) {
        this.constructionDescription = description;
    }

    @Override
    public double getValue(DataRow row) {
        double result = row.get(getTableIndex(), defaultValue);
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
        row.set(getTableIndex(), newValue, defaultValue);
    }

    @Override
    public void addTransformation(AttributeTransformation transformation) {
        transformations.add(transformation);
    }

    @Override
    public void clearTransformations() {
        this.transformations.clear();
    }

    /**
     * Returns the value type of this attribute.
     *
     * @see Ontology#ATTRIBUTE_VALUE_TYPE
     */
    @Override
    public int getValueType() {
        return valueType;
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
}
