package io.sugo.pio.operator.preprocessing.filter;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.SetRelation;
import io.sugo.pio.tools.Ontology;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Converts all numerical attributes to nominal ones.
 */
public abstract class NumericToNominal extends AbstractFilteredDataProcessing {

    public NumericToNominal() {
        super();
    }

    protected abstract void setValue(Example example, Attribute newAttribute, double value) throws OperatorException;

    /**
     * Returns {@link Ontology#NOMINAL} or one of its subtypes.
     */
    protected abstract int getGeneratedAttributevalueType();

    protected Attribute makeAttribute() {
        return AttributeFactory.createAttribute(getGeneratedAttributevalueType());
    }

    /**
     * Will be invoked before the setValue method is invoked for each example. This default
     * implementation does nothing.
     */
    public void init() throws OperatorException {
    }

    /**
     * Will be invoked after the setValue method was invoked for each example. This default
     * implementation does nothing.
     */
    public void cleanUp() throws OperatorException {
    }

    @Override
    public ExampleSetMetaData applyOnFilteredMetaData(ExampleSetMetaData emd) throws UndefinedParameterError {
        for (AttributeMetaData amd : emd.getAllAttributes()) {
            if (!amd.isSpecial() && amd.isNumerical()) {
                amd.setType(Ontology.NOMINAL);
                amd.setValueSetRelation(SetRelation.SUPERSET);
            }
        }
        return emd;
    }

    @Override
    public ExampleSet applyOnFiltered(ExampleSet exampleSet) throws OperatorException {
        Map<Attribute, Attribute> translationMap = new LinkedHashMap<Attribute, Attribute>();
        // creating new nominal attributes
        for (Attribute originalAttribute : exampleSet.getAttributes()) {

            if (originalAttribute.isNumerical()) {
                Attribute newAttribute = makeAttribute();
                translationMap.put(originalAttribute, newAttribute);
            }
        }
        // adding to table and exampleSet
        for (Entry<Attribute, Attribute> replacement : translationMap.entrySet()) {
            Attribute newAttribute = replacement.getValue();
            exampleSet.getExampleTable().addAttribute(newAttribute);
            exampleSet.getAttributes().addRegular(newAttribute);
        }

        // invoke init
        init();

        // initialize progress
        getProgress().setTotal(exampleSet.size());
        int numberOfAttributes = exampleSet.getAttributes().allSize();
        int progressTriggerCounter = 0;
        int progressCompletedCounter = 0;

        // over all examples change attribute values
        for (Example example : exampleSet) {
            for (Entry<Attribute, Attribute> replacement : translationMap.entrySet()) {
                Attribute oldAttribute = replacement.getKey();
                Attribute newAttribute = replacement.getValue();
                double oldValue = example.getValue(oldAttribute);
                setValue(example, newAttribute, oldValue);
            }
            ++progressCompletedCounter;
            ++progressTriggerCounter;
            if (getProgress().getProgress() / 20 < (int) (progressCompletedCounter * 5L / exampleSet.size())
                    || progressTriggerCounter * numberOfAttributes > 500000) {
                progressTriggerCounter = 0;
                getProgress().setCompleted(progressCompletedCounter);
            }
        }

        // clean up
        cleanUp();

        // removing old attributes
        for (Entry<Attribute, Attribute> entry : translationMap.entrySet()) {
            Attribute originalAttribute = entry.getKey();
            exampleSet.getAttributes().remove(originalAttribute);
            entry.getValue().setName(originalAttribute.getName());
        }

        return exampleSet;
    }

    /**
     * This adds another column, does not modify existing.
     */
    @Override
    public boolean writesIntoExistingData() {
        return false;
    }

    @Override
    protected int[] getFilterValueTypes() {
        return new int[]{Ontology.NUMERICAL};
    }
}
