package io.sugo.pio.example;

import io.sugo.pio.tools.Ontology;

import java.util.Iterator;

/**
 */
public class ExampleTools {
    public static String[] getAllAttributeNames(ExampleSet exampleSet) {
        String[] attributeNames = new String[exampleSet.getAttributes().allSize()];
        int counter = 0;
        Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
        while (a.hasNext()) {
            Attribute attribute = a.next();
            attributeNames[counter++] = attribute.getName();
        }
        return attributeNames;
    }

    public static String[] getRegularAttributeNames(ExampleSet exampleSet) {
        String[] attributeNames = new String[exampleSet.getAttributes().size()];
        int counter = 0;
        for (Attribute attribute : exampleSet.getAttributes()) {
            attributeNames[counter++] = attribute.getName();
        }
        return attributeNames;
    }

    public static boolean containsValueType(ExampleSet exampleSet, int valueType) {
        for (Attribute attribute : exampleSet.getAttributes()) {
            if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), valueType)) {
                return true;
            }
        }
        return false;
    }
}
