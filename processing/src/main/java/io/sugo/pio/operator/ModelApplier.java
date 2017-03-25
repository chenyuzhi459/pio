package io.sugo.pio.operator;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.error.UnsupportedApplicationParameterError;
import io.sugo.pio.operator.preprocessing.PreprocessingOperator;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeList;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 */
public class ModelApplier extends Operator {
//    public ModelApplier(String name) {
//
//    }

    private static final Logger logger = new Logger(ModelApplier.class);

    /**
     * The parameter name for &quot;key&quot;
     */
    public static final String PARAMETER_KEY = "key";

    /**
     * The parameter name for &quot;value&quot;
     */
    public static final String PARAMETER_VALUE = "value";

    /**
     * The possible parameters used by the model during application time.
     */
    public static final String PARAMETER_APPLICATION_PARAMETERS = "application_parameters";

    /**
     * Indicates if preprocessing models should create a view instead of changing the data.
     */
    private static final String PARAMETER_CREATE_VIEW = "create_view";

    private final InputPort modelInput = getInputPorts().createPort(PortType.MODEL);
    private final InputPort exampleSetInput = getInputPorts().createPort(PortType.UNLABELLED_DATA);
    private final OutputPort exampleSetOutput = getOutputPorts().createPort(PortType.LABELLED_DATA);
    private final OutputPort modelOutput = getOutputPorts().createPort(PortType.MODEL);

    public ModelApplier() {
//        super("modelApplier");
        modelInput.addPrecondition(
                new SimplePrecondition(modelInput, new ModelMetaData(Model.class, new ExampleSetMetaData())));
        exampleSetInput.addPrecondition(new SimplePrecondition(exampleSetInput, new ExampleSetMetaData()));
        getTransformer().addRule(new ModelApplicationRule(exampleSetInput, exampleSetOutput, modelInput, false));
        getTransformer().addRule(new PassThroughRule(modelInput, modelOutput, false));
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(exampleSetOutput.getAnyDataOrNull());
        ioObjects.add(modelOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.ModelApplier.name");
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.ModelApplier.description");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    /**
     * Applies the operator and labels the {@link ExampleSet}. The example set in the input is not
     * consumed.
     */
    @Override
    public void doWork() throws OperatorException {
        ExampleSet inputExampleSet = exampleSetInput.getData(ExampleSet.class);
        Model model = modelInput.getData(Model.class);

        logger.info("Begin to apply model[%s]...", model.getName());

        if (AbstractModel.class.isAssignableFrom(model.getClass())) {
            ((AbstractModel) model).setOperator(this);
            ((AbstractModel) model).setShowProgress(true);
        }

        List<String[]> modelParameters = getParameterList(PARAMETER_APPLICATION_PARAMETERS);
        Iterator<String[]> i = modelParameters.iterator();
        while (i.hasNext()) {
            String[] parameter = i.next();
            try {
                model.setParameter(parameter[0], parameter[1]);
            } catch (UnsupportedApplicationParameterError e) {
                e.setOperator(this);
                throw e;
            }
        }

        // handling PreprocessingModels: extra treatment for views
        if (getParameterAsBoolean(PARAMETER_CREATE_VIEW)) {
            try {
                model.setParameter(PreprocessingOperator.PARAMETER_CREATE_VIEW, true);
            } catch (UnsupportedApplicationParameterError e) {
                e.setOperator(this);
                throw e;
            }
        }

        ExampleSet result = inputExampleSet;
        try {
            result = model.apply(inputExampleSet);
            logger.info("Apply model[%s] to example set[%s]", model.getName(), inputExampleSet.getName());
        } catch (UserError e) {
            if (e.getOperator() == null) {
                e.setOperator(this);
            }
            throw e;
        }

        if (AbstractModel.class.isAssignableFrom(model.getClass())) {
            ((AbstractModel) model).setOperator(null);
            ((AbstractModel) model).setShowProgress(false);
        }

        exampleSetOutput.deliver(result);
        modelOutput.deliver(model);
    }

    @Override
    public boolean shouldAutoConnect(OutputPort port) {
        if (port == modelOutput) {
            return getParameterAsBoolean("keep_model");
        } else {
            return super.shouldAutoConnect(port);
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        /*types.add(new ParameterTypeList(PARAMETER_APPLICATION_PARAMETERS,
                I18N.getMessage("pio.ModelApplier.application_parameters"),
//                "Model parameters for application (usually not needed).",
                new ParameterTypeString(PARAMETER_KEY,
                        I18N.getMessage("pio.ModelApplier.key")
//                        "The model parameter key."
                ),
                new ParameterTypeString(PARAMETER_VALUE,
                        I18N.getMessage("pio.ModelApplier.value")
//                        "This key's value"
                )
        ));
        types.add(new ParameterTypeBoolean(PARAMETER_CREATE_VIEW,
                I18N.getMessage("pio.ModelApplier.create_view"),
//                "Indicates that models should create a new view on the data where possible. Then, instead of changing the data itself, the results are calculated on the fly if needed.",
                false));*/
        return types;
    }
}
