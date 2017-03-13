package io.sugo.pio.example.set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.*;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.Annotations;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This example set uses the mapping given by another example set and "remaps" on the fly the
 * nominal values according to the given set. It also sorts the regular attributes in the order of
 * the other exampleSet if possible. If additional attributes occur, they are appended on the end of
 * the example set, if keepAdditional is selected.
 */
public class RemappedExampleSet extends AbstractExampleSet {

    private static final long serialVersionUID = 3460640319989955936L;

    @JsonProperty
    private ExampleSet parent;

    public RemappedExampleSet(ExampleSet parentSet, ExampleSet mappingSet) {
        this(parentSet, mappingSet, true);
    }

    public RemappedExampleSet(ExampleSet parentSet, ExampleSet _mappingSet, boolean keepAdditional) {
        this.parent = (ExampleSet) parentSet.clone();
        ExampleSet mappingSet = (ExampleSet) _mappingSet.clone();

        // check for a missing mappingSet because of compatibility
        if (mappingSet != null) {
            Attributes attributes = parent.getAttributes();

            // copying attributes into name map
            Map<String, Attribute> attributeMap = new LinkedHashMap<>(parent.size());
            for (Attribute attribute : attributes) {
                attributeMap.put(attribute.getName(), attribute);
            }

            // clearing cloned set
            attributes.clearRegular();

            // adding again in mappingSets's order
            for (Attribute mapAttribute : mappingSet.getAttributes()) {
                String name = mapAttribute.getName();
                Attribute attribute = attributeMap.get(name);
                if (attribute != null) {
                    attributes.addRegular(attribute);
                    attributeMap.remove(name);
                }
            }

            if (keepAdditional) {
                // adding all additional attributes
                for (Attribute attribute : attributeMap.values()) {
                    attributes.addRegular(attribute);
                }
            }

            // mapping nominal values
            Iterator<AttributeRole> a = this.parent.getAttributes().allAttributeRoles();
            while (a.hasNext()) {
                AttributeRole role = a.next();
                Attribute currentAttribute = role.getAttribute();
                if (currentAttribute.isNominal()) {
                    NominalMapping mapping = null;
                    mapping = currentAttribute.getMapping();
                    Attribute oldMappingAttribute = mappingSet.getAttributes().get(role.getAttribute().getName());
                    if (oldMappingAttribute != null && oldMappingAttribute.isNominal()) {
                        mapping = oldMappingAttribute.getMapping();
                    }
                    currentAttribute.addTransformation(new AttributeTransformationRemapping(mapping));
                }
            }
        }
    }

    /**
     * Clone constructor.
     */
    public RemappedExampleSet(RemappedExampleSet other) {
        this.parent = (ExampleSet) other.parent.clone();
    }

    @Override
    public Attributes getAttributes() {
        return this.parent.getAttributes();
    }

    @Override
    public ExampleTable getExampleTable() {
        return parent.getExampleTable();
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public Iterator<Example> iterator() {
        return new AttributesExampleReader(parent.iterator(), this);
    }

    @Override
    public Example getExample(int index) {
        return this.parent.getExample(index);
    }

    @Override
    public Annotations getAnnotations() {
        return parent.getAnnotations();
    }

    @Override
    public void cleanup() {
        parent.cleanup();
    }
}
