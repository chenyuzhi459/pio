package io.sugo.pio.operator.preprocessing;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.NonSpecialAttributesExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.GenerateModelTransformationRule;
import io.sugo.pio.tools.AttributeSubsetSelector;
import io.sugo.pio.tools.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Superclass for all preprocessing operators. Classes which extend this class must implement the
 * method {@link #createPreprocessingModel(ExampleSet)} . This method can also be returned by this
 * operator and will be combined with other models.
 *
 */
public abstract class PreprocessingOperator extends AbstractDataProcessing {

    private static final Logger logger = new Logger(PreprocessingOperator.class);

    private final OutputPort modelOutput = getOutputPorts().createPort(PortType.PREPROCESSING_MODEL);

    protected final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getExampleSetInputPort(),
            getFilterValueTypes());

    /**
     * The parameter name for &quot;Indicates if the preprocessing model should also be
     * returned&quot;
     */
    public static final String PARAMETER_RETURN_PREPROCESSING_MODEL = "return_preprocessing_model";

    /**
     * Indicates if this operator should create a view (new example set on the view stack) instead
     * of directly changing the data.
     */
    public static final String PARAMETER_CREATE_VIEW = "create_view";

    public PreprocessingOperator() {
        getTransformer().addRule(
                new GenerateModelTransformationRule(getExampleSetInputPort(), modelOutput, getPreprocessingModelClass()));
        getExampleSetInputPort().addPrecondition(attributeSelector.makePrecondition());
    }

    /**
     * Subclasses might override this method to define the meta data transformation performed by
     * this operator. The default implementation takes all attributes specified by the
     * {@link AttributeSubsetSelector} and passes them to
     * {@link #modifyAttributeMetaData(ExampleSetMetaData, AttributeMetaData)} and replaces them
     * accordingly.
     *
     * @throws UndefinedParameterError
     */
    @Override
    protected ExampleSetMetaData modifyMetaData(ExampleSetMetaData exampleSetMetaData) throws UndefinedParameterError {
        ExampleSetMetaData subsetMetaData = attributeSelector.getMetaDataSubset(exampleSetMetaData,
                isSupportingAttributeRoles());
        checkSelectedSubsetMetaData(subsetMetaData);
        for (AttributeMetaData amd : subsetMetaData.getAllAttributes()) {
            Collection<AttributeMetaData> replacement = null;
            replacement = modifyAttributeMetaData(exampleSetMetaData, amd);
            if (replacement != null) {
                if (replacement.size() == 1) {
                    AttributeMetaData replacementAttribute = replacement.iterator().next();
                    replacementAttribute.setRole(exampleSetMetaData.getAttributeByName(amd.getName()).getRole());
                }
                exampleSetMetaData.removeAttribute(amd);
                exampleSetMetaData.addAllAttributes(replacement);
            }
        }
        return exampleSetMetaData;
    }

    /**
     * Can be overridden to check the selected attributes for compatibility.
     */
    protected void checkSelectedSubsetMetaData(ExampleSetMetaData subsetMetaData) {
    }

    /**
     * If this preprocessing operator generates new attributes, the corresponding meta data should
     * be returned by this method. The attribute will be replaced by the collection. If this
     * operator modifies a single one, amd itself should be modified as a side effect and null
     * should be returned. Note: If an empty collection is returned, amd will be removed, but no new
     * attribute will be added.
     **/
    protected abstract Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd)
            throws UndefinedParameterError;

    public abstract PreprocessingModel createPreprocessingModel(ExampleSet exampleSet) throws OperatorException;

    /**
     * This method allows subclasses to easily get a collection of the affected attributes.
     *
     * @throws UndefinedParameterError
     * @throws UserError
     */
    protected final ExampleSet getSelectedAttributes(ExampleSet exampleSet) throws UndefinedParameterError, UserError {
        return attributeSelector.getSubset(exampleSet, isSupportingAttributeRoles());
    }

    @Override
    public final ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        ExampleSet workingSet = isSupportingAttributeRoles() ? getSelectedAttributes(exampleSet)
                : new NonSpecialAttributesExampleSet(getSelectedAttributes(exampleSet));

        AbstractModel model = createPreprocessingModel(workingSet);
        model.setParameter(PARAMETER_CREATE_VIEW, getParameterAsBoolean(PARAMETER_CREATE_VIEW));
        if (getExampleSetOutputPort().isConnected()) {
            model.setOperator(this);
            model.setShowProgress(true);
            exampleSet = model.apply(exampleSet);
            model.setOperator(null);
            model.setShowProgress(false);
        }

        modelOutput.deliver(model);
        logger.info("PreprocessingOperator apply and deliver model successfully.");

        return exampleSet;
    }

    /**
     * Helper wrapper for {@link #exampleSetInput that can be called by other operators to apply
     * this operator when it is created anonymously.
     */
    public ExampleSet doWork(ExampleSet exampleSet) throws OperatorException {
        ExampleSet workingSet = isSupportingAttributeRoles() ? getSelectedAttributes(exampleSet)
                : new NonSpecialAttributesExampleSet(getSelectedAttributes(exampleSet));

        AbstractModel model = createPreprocessingModel(workingSet);
        model.setParameter(PARAMETER_CREATE_VIEW, getParameterAsBoolean(PARAMETER_CREATE_VIEW));
        model.setOperator(this);
        return model.apply(exampleSet);
    }

    public Pair<ExampleSet, Model> doWorkModel(ExampleSet exampleSet) throws OperatorException {
        exampleSet = apply(exampleSet);
        Model model = modelOutput.getData(Model.class);
        return new Pair<>(exampleSet, model);
    }

    @Override
    public boolean writesIntoExistingData() {
        return !getParameterAsBoolean(PARAMETER_CREATE_VIEW);
    }

    @Override
    public boolean shouldAutoConnect(OutputPort outputPort) {
        if (outputPort == modelOutput) {
            return getParameterAsBoolean(PARAMETER_RETURN_PREPROCESSING_MODEL);
        } else {
            return super.shouldAutoConnect(outputPort);
        }
    }

    /**
     * Defines the value types of the attributes which are processed or affected by this operator.
     * Has to be overridden to restrict the attributes which can be chosen by an
     * {@link AttributeSubsetSelector}.
     *
     * @return array of value types
     */
    protected abstract int[] getFilterValueTypes();

    public abstract Class<? extends PreprocessingModel> getPreprocessingModelClass();

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeBoolean(PARAMETER_RETURN_PREPROCESSING_MODEL,
                I18N.getMessage("pio.PreprocessingOperator.return_preprocessing_model"), false);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_CREATE_VIEW,
                I18N.getMessage("pio.PreprocessingOperator.create_view"), false);
        type.setHidden(!isSupportingView());
        types.add(type);

        types.addAll(attributeSelector.getParameterTypes());
        return types;
    }

    @Override
    public IOContainer getResult() {
        IOContainer container = super.getResult();

        List<IOObject> ioObjects = container.getIoObjects();
        ioObjects.add(modelOutput.getAnyDataOrNull());

        return container;
    }

    /**
     * Subclasses which need to have the attribute roles must return true. Otherwise all selected
     * attributes are converted into regular and afterwards given their old roles.
     */
    public boolean isSupportingAttributeRoles() {
        return false;
    }

    /**
     * Subclasses might overwrite this in order to hide the create_view parameter
     *
     * @return
     */
    public boolean isSupportingView() {
        return true;
    }

    public OutputPort getPreprocessingModelOutputPort() {
        return modelOutput;
    }
}
