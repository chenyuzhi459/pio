package io.sugo.pio.operator.preprocessing.filter;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeRole;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.NonSpecialAttributesExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.tools.AttributeSubsetSelector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * This class is for preprocessing operators, which can be restricted to use only a subset of the
 * attributes. The MetaData is changed accordingly in a way equivalent to surrounding the operator
 * with an AttributeSubsetPreprocessing operator. Subclasses must overwrite the methods
 * {@link #applyOnFiltered(ExampleSet)} and {@link #applyOnFilteredMetaData(ExampleSetMetaData)} in
 * order to provide their functionality and the correct meta data handling.
 */
public abstract class AbstractFilteredDataProcessing extends AbstractDataProcessing {

    private static final Logger logger = new Logger(AbstractFilteredDataProcessing.class);

    private static final String PARAMETER_ATTRIBUTES = "attributes";

    private final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getExampleSetInputPort(),
            getFilterValueTypes());

    public AbstractFilteredDataProcessing() {
        super();
    }

    private static final OperatorVersion FAIL_ON_MISSING_ATTRIBUTES = new OperatorVersion(6, 0, 3);

	/*@Override
    public OperatorVersion[] getIncompatibleVersionChanges() {
		OperatorVersion[] odlOne = super.getIncompatibleVersionChanges();
		OperatorVersion[] newOne = Arrays.copyOf(odlOne, odlOne.length + 1);
		newOne[odlOne.length] = FAIL_ON_MISSING_ATTRIBUTES;
		return newOne;
	}*/

    @Override
    protected final MetaData modifyMetaData(ExampleSetMetaData inputMetaData) {
        ExampleSetMetaData workingMetaData = inputMetaData.clone();

        ExampleSetMetaData subsetAmd = attributeSelector.getMetaDataSubset(workingMetaData, false);

        // storing unused attributes and saving roles
        List<AttributeMetaData> unusedAttributes = new LinkedList<>();
        Iterator<AttributeMetaData> iterator = workingMetaData.getAllAttributes().iterator();
        while (iterator.hasNext()) {
            AttributeMetaData amd = iterator.next();
            String name = amd.getName();
            MetaDataInfo containsAttributeName = subsetAmd.containsAttributeName(name);

            if (subsetAmd.getAttributeSetRelation() == SetRelation.SUBSET && containsAttributeName == MetaDataInfo.NO
                    || subsetAmd.getAttributeSetRelation() != SetRelation.SUBSET
                    && containsAttributeName != MetaDataInfo.YES) {
                unusedAttributes.add(amd);
                iterator.remove();
            } else if (amd.isSpecial()) {
                amd.setRegular();
            }
        }

        // retrieving result
        ExampleSetMetaData resultMetaData = workingMetaData;
        try {
            resultMetaData = applyOnFilteredMetaData(workingMetaData);
        } catch (UndefinedParameterError e) {
        }

        // merge result with unusedAttributes: restore special types from original input
        Iterator<AttributeMetaData> r = resultMetaData.getAllAttributes().iterator();
        while (r.hasNext()) {
            AttributeMetaData newMetaData = r.next();
            AttributeMetaData oldMetaData = inputMetaData.getAttributeByName(newMetaData.getName());
            if (oldMetaData != null) {
                if (oldMetaData.isSpecial()) {
                    String specialName = oldMetaData.getRole();
                    newMetaData.setRole(specialName);
                }
            }
        }

        // add unused attributes again
        resultMetaData.addAllAttributes(unusedAttributes);
        return resultMetaData;
    }

    @Override
    /**
     * This method filters the attributes according to the AttributeSubsetSelector and
     * then applies the operation of the subclass on this data. Finally the changed data is merged
     * back into the exampleSet. This is done in the AttributeSubsetPreprocessing way and somehow doubles the
     * code.
     */
    public final ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        ExampleSet workingExampleSet = (ExampleSet) exampleSet.clone();
        /*Set<Attribute> selectedAttributes = attributeSelector.getAttributeSubset(workingExampleSet, false, this
				.getCompatibilityLevel().isAtMost(FAIL_ON_MISSING_ATTRIBUTES) ? false : true);*/
        Set<Attribute> selectedAttributes = attributeSelector.getAttributeSubset(workingExampleSet, false, false);

        List<Attribute> unusedAttributes = new LinkedList<>();
        Iterator<Attribute> iterator = workingExampleSet.getAttributes().allAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            if (!selectedAttributes.contains(attribute)) {
                unusedAttributes.add(attribute);
                iterator.remove();
            }
        }

        // converting special to normal
        workingExampleSet = new NonSpecialAttributesExampleSet(workingExampleSet);

        // applying filtering
        ExampleSet resultSet = applyOnFiltered(workingExampleSet);

        // transform special attributes back
        Iterator<AttributeRole> r = resultSet.getAttributes().allAttributeRoles();
        while (r.hasNext()) {
            AttributeRole newRole = r.next();
            AttributeRole oldRole = exampleSet.getAttributes().getRole(newRole.getAttribute().getName());
            if (oldRole != null) {
                if (oldRole.isSpecial()) {
                    String specialName = oldRole.getSpecialName();
                    newRole.setSpecial(specialName);
                }
            }
        }

        // add old attributes if desired
        if (resultSet.size() != exampleSet.size()) {
            throw new UserError(this, "pio.error.process.setup_error",
                    "changing the size of the example set is not allowed if the non-processed attributes should be kept.");
        }

        if (resultSet.getExampleTable().equals(exampleSet.getExampleTable())) {
            for (Attribute attribute : unusedAttributes) {
                AttributeRole role = exampleSet.getAttributes().getRole(attribute);
                resultSet.getAttributes().add(role);
            }
        } else {
            String message = "Underlying example table has changed: data copy into new table is necessary in order to keep non-processed attributes.";
            logger.warn(message);
            collectWarnLog(message);
            for (Attribute oldAttribute : unusedAttributes) {
                AttributeRole oldRole = exampleSet.getAttributes().getRole(oldAttribute);

                // create and add copy of attribute
                Attribute newAttribute = (Attribute) oldAttribute.clone();
                resultSet.getExampleTable().addAttribute(newAttribute);
                AttributeRole newRole = new AttributeRole(newAttribute);
                if (oldRole.isSpecial()) {
                    newRole.setSpecial(oldRole.getSpecialName());
                }
                resultSet.getAttributes().add(newRole);

                // copy data for the new attribute
                Iterator<Example> oldIterator = exampleSet.iterator();
                Iterator<Example> newIterator = resultSet.iterator();
                while (oldIterator.hasNext()) {
                    Example oldExample = oldIterator.next();
                    Example newExample = newIterator.next();
                    newExample.setValue(newAttribute, oldExample.getValue(oldAttribute));
                }
            }
        }
        return resultSet;
    }

    /**
     * Subclasses have to implement this method in order to operate only on the selected attributes.
     * The results are merged back into the original example set.
     */
    public abstract ExampleSet applyOnFiltered(ExampleSet exampleSet) throws OperatorException;

    /**
     * This method has to be implemented in order to specify the changes of the meta data caused by
     * the application of this operator.
     */
    public abstract ExampleSetMetaData applyOnFilteredMetaData(ExampleSetMetaData emd) throws UndefinedParameterError;

    /**
     * Defines the value types of the attributes which are processed or affected by this operator.
     * Has to be overridden to restrict the attributes which can be chosen by an
     * {@link AttributeSubsetSelector}.
     *
     * @return array of value types
     */
    protected abstract int[] getFilterValueTypes();

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.addAll(attributeSelector.getParameterTypes());

        types.forEach(parameterType -> {
            if (PARAMETER_ATTRIBUTES.equals(parameterType.getKey())) {
                parameterType.setDescription(I18N.getMessage("pio.NumericToPolynominal.attributes_desc"));
                parameterType.setFullName(I18N.getMessage("pio.NumericToPolynominal.attributes_desc"));
            }
        });
        return types;
    }
}
