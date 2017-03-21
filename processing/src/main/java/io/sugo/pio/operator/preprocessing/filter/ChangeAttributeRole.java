package io.sugo.pio.operator.preprocessing.filter;


import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.error.AttributeNotFoundError;
import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeAttribute;
import io.sugo.pio.parameter.ParameterTypeStringCategory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.AttributeSetPrecondition;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;

import java.util.List;

/**
 * <p>
 * This operator can be used to change the attribute type of an attribute of the input example set.
 * If you want to change the attribute name you should use the {@link } operator.
 * </p>
 * <p>
 * <p>
 * The target type indicates if the attribute is a regular attribute (used by learning operators) or
 * a special attribute (e.g. a label or id attribute). The following target attribute types are
 * possible:
 * </p>
 * <ul>
 * <li>regular: only regular attributes are used as input variables for learning tasks</li>
 * <li>id: the id attribute for the example set</li>
 * <li>label: target attribute for learning</li>
 * <li>prediction: predicted attribute, i.e. the predictions of a learning scheme</li>
 * <li>cluster: indicates the membership to a cluster</li>
 * <li>weight: indicates the weight of the example</li>
 * <li>batch: indicates the membership to an example batch</li>
 * </ul>
 * <p>
 * Users can also define own attribute types by simply using the desired name.
 * </p>
 * <p>
 * If the target attribute type is already in use the operator will since Version 5.3.13 change the
 * Type of the attribute to regular and changes the type of the given Attribute to the target
 * attribute type. Before these Version the Operator will delete the attribute which already has the
 * role.
 * </p>
 */
public class ChangeAttributeRole extends AbstractDataProcessing {

    private static final Logger logger = new Logger(ChangeAttributeRole.class);

    /**
     * The parameter name for &quot;The name of the attribute of which the type should be
     * changed.&quot;
     */
    public static final String PARAMETER_NAME = "attribute_name";

    /**
     * The parameter name for &quot;The target type of the attribute (only changed if parameter
     * change_attribute_type is true).&quot;
     */
    public static final String PARAMETER_TARGET_ROLE = "target_role";

    public static final String PARAMETER_CHANGE_ATTRIBUTES = "set_additional_roles";

    private static final String REGULAR_NAME = "regular";
    private static final String REGULAR_NAME_DESC = I18N.getMessage("pio.ChangeAttributeRole.regular_name");

    /*private static final String[] TARGET_ROLES = new String[]{REGULAR_NAME, Attributes.ID_NAME, Attributes.LABEL_NAME,
            Attributes.PREDICTION_NAME, Attributes.CLUSTER_NAME, Attributes.WEIGHT_NAME, Attributes.BATCH_NAME};*/
    private static final String[] TARGET_ROLES = new String[]{REGULAR_NAME, Attributes.ID_NAME, Attributes.LABEL_NAME,
            Attributes.PREDICTION_NAME};
    private static final String[] TARGET_ROLES_DESC = new String[]{REGULAR_NAME_DESC, Attributes.ID_NAME_DESC,
            Attributes.LABEL_NAME_DESC, Attributes.PREDICTION_NAME_DESC};

//    private final OperatorVersion VERSION_BEFORE_KEEPING_SPECIAL_ATT_WHEN_IT_LOSE_ROLE = new OperatorVersion(5, 3, 13);

    public ChangeAttributeRole() {
        getExampleSetInputPort().addPrecondition(
                new AttributeSetPrecondition(getExampleSetInputPort(), AttributeSetPrecondition.getAttributesByParameter(
                        this, PARAMETER_NAME)));
        getExampleSetInputPort().addPrecondition(
                new AttributeSetPrecondition(getExampleSetInputPort(), AttributeSetPrecondition
                        .getAttributesByParameterListEntry(this, PARAMETER_CHANGE_ATTRIBUTES, 0)));
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.ChangeAttributeRole.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.ChangeAttributeRole.description");
    }

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) {
        try {
            String targetRole = null;
            if (isParameterSet(PARAMETER_TARGET_ROLE)) {
                targetRole = getParameterAsString(PARAMETER_TARGET_ROLE);
            }

            if (isParameterSet(PARAMETER_NAME)) {
                String name = getParameter(PARAMETER_NAME);
                setRoleMetaData(metaData, name, targetRole);
            }

            // now proceed with list
            if (isParameterSet(PARAMETER_CHANGE_ATTRIBUTES)) {
                List<String[]> list = getParameterList(PARAMETER_CHANGE_ATTRIBUTES);
                for (String[] pairs : list) {
                    setRoleMetaData(metaData, pairs[0], pairs[1]);
                }
            }
        } catch (UndefinedParameterError e) {
        }
        return metaData;
    }

    private void setRoleMetaData(ExampleSetMetaData metaData, String name, String targetRole) {
        AttributeMetaData amd = metaData.getAttributeByName(name);
        if (amd != null) {
            if (targetRole != null) {
                if (REGULAR_NAME.equals(targetRole)) {
                    amd.setRegular();
                } else {
                    AttributeMetaData oldRole = metaData.getAttributeByRole(targetRole);
                    if (oldRole != null && oldRole != amd) {
                        oldRole.setRegular();
//                        if (getCompatibilityLevel().compareTo(VERSION_BEFORE_KEEPING_SPECIAL_ATT_WHEN_IT_LOSE_ROLE) > 0) {
//                        } else {
//                            getInputPort().addError(
//                                    new SimpleMetaDataError(Severity.WARNING, getInputPort(), "already_contains_role",
//                                            targetRole));
//                            metaData.removeAttribute(oldRole);
//                        }
                    }
                    amd.setRole(targetRole);
                }
            }
        }
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        logger.info("Change attribute role begin to apply example set[%s]...", exampleSet.getName());

        String name = getParameterAsString(PARAMETER_NAME);
        String newRole = getParameterAsString(PARAMETER_TARGET_ROLE);

        setRole(exampleSet, name, newRole, PARAMETER_NAME);

        // now do the list
        if (isParameterSet(PARAMETER_CHANGE_ATTRIBUTES)) {
            List<String[]> list = getParameterList(PARAMETER_CHANGE_ATTRIBUTES);
            for (String[] pairs : list) {
                setRole(exampleSet, pairs[0], pairs[1], PARAMETER_CHANGE_ATTRIBUTES);
            }
        }

        return exampleSet;
    }

    private void setRole(ExampleSet exampleSet, String name, String newRole, String paramKey) throws UserError {
        Attribute attribute = exampleSet.getAttributes().get(name);

        if (attribute == null) {
            throw new AttributeNotFoundError(this, paramKey, name);
        }

        exampleSet.getAttributes().remove(attribute);
        if (newRole == null || newRole.trim().length() == 0) {
            throw new UndefinedParameterError(PARAMETER_TARGET_ROLE, this);
        }
        if (newRole.equals(REGULAR_NAME)) {
            exampleSet.getAttributes().addRegular(attribute);
        } else {
//            if (getCompatibilityLevel().compareTo(VERSION_BEFORE_KEEPING_SPECIAL_ATT_WHEN_IT_LOSE_ROLE) > 0) {
            Attribute oldOne = exampleSet.getAttributes().getSpecial(newRole);
            if (oldOne != null) {
                exampleSet.getAttributes().remove(oldOne);
                exampleSet.getAttributes().addRegular(oldOne);
            }
//            }
            exampleSet.getAttributes().setSpecialAttribute(attribute, newRole);
        }

        logger.info("Change attribute role set new role[%s] of example set[%s] successfully!", newRole, exampleSet.getName());
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeAttribute(PARAMETER_NAME,
                I18N.getMessage("pio.ChangeAttributeRole.attribute_name"),
                getExampleSetInputPort(), false, false));
        ParameterType type = new ParameterTypeStringCategory(PARAMETER_TARGET_ROLE,
                I18N.getMessage("pio.ChangeAttributeRole.target_role"), TARGET_ROLES, TARGET_ROLES_DESC,
                TARGET_ROLES[0], true);
//		type.setExpert(false);
        types.add(type);

        /*types.add(new ParameterTypeList(PARAMETER_CHANGE_ATTRIBUTES,
                I18N.getMessage("pio.ChangeAttributeRole.set_additional_roles"),
                new ParameterTypeAttribute(PARAMETER_NAME,
                        I18N.getMessage("pio.ChangeAttributeRole.attribute_name"),
                        getExampleSetInputPort(), false, false),
                new ParameterTypeStringCategory(PARAMETER_TARGET_ROLE,
                        I18N.getMessage("pio.ChangeAttributeRole.target_role"),
                        TARGET_ROLES, TARGET_ROLES[0])));*/
        return types;
    }

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }
}
