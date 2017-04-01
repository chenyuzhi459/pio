package io.sugo.pio.example.set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.AttributeRole;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.operator.Annotations;

import java.util.Iterator;

/**
 * This example set treats all special attributes as regular attributes.
 *
 */
public class NonSpecialAttributesExampleSet extends AbstractExampleSet {

    private static final long serialVersionUID = -4782316585512718459L;

    /**
     * The parent example set.
     */
    private ExampleSet parent;

    public NonSpecialAttributesExampleSet(ExampleSet exampleSet) {
        this.parent = (ExampleSet) exampleSet.clone();
        Iterator<AttributeRole> s = this.parent.getAttributes().specialAttributes();
        while (s.hasNext()) {
            AttributeRole attributeRole = s.next();
            if (attributeRole.isSpecial()) {
                attributeRole.changeToRegular();
            }
        }
    }

    /**
     * Clone constructor.
     */
    public NonSpecialAttributesExampleSet(NonSpecialAttributesExampleSet exampleSet) {
        this.parent = (ExampleSet) exampleSet.parent.clone();
    }

    @Override
    public Attributes getAttributes() {
        return this.parent.getAttributes();
    }

    /**
     * Creates an iterator over all examples.
     */
    @Override
    public Iterator<Example> iterator() {
        return new AttributesExampleReader(parent.iterator(), this);
    }

    @Override
    @JsonProperty
    public ExampleTable getExampleTable() {
        return parent.getExampleTable();
    }

    @Override
    public Example getExample(int index) {
        return this.parent.getExample(index);
    }

    @Override
    @JsonProperty
    public int size() {
        return parent.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.sugo.pio.operator.ResultObjectAdapter#getAnnotations()
     */
    @Override
    public Annotations getAnnotations() {
        return parent.getAnnotations();
    }

    @Override
    public void cleanup() {
        parent.cleanup();
    }
}
