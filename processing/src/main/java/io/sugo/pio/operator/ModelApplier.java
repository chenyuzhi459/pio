package io.sugo.pio.operator;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeList;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;
import java.util.List;

/**
 */
public class ModelApplier extends Operator {
    public ModelApplier(String name) {
        super(name);
    }

    /** The parameter name for &quot;key&quot; */
    public static final String PARAMETER_KEY = "key";

    /** The parameter name for &quot;value&quot; */
    public static final String PARAMETER_VALUE = "value";

    /** The possible parameters used by the model during application time. */
    public static final String PARAMETER_APPLICATION_PARAMETERS = "application_parameters";

    /** Indicates if preprocessing models should create a view instead of changing the data. */
    private static final String PARAMETER_CREATE_VIEW = "create_view";

    private final InputPort modelInput = getInputPorts().createPort("model");
    private final InputPort exampleSetInput = getInputPorts().createPort("unlabelled data");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("labelled data");
    private final OutputPort modelOutput = getOutputPorts().createPort("model");

    public ModelApplier() {
        super("modelApplier");
//        modelInput.addPrecondition(
//                new SimplePrecondition(modelInput, new ModelMetaData(Model.class, new ExampleSetMetaData())));
//        exampleSetInput.addPrecondition(new SimplePrecondition(exampleSetInput, new ExampleSetMetaData()));
//        getTransformer().addRule(new ModelApplicationRule(exampleSetInput, exampleSetOutput, modelInput, false));
//        getTransformer().addRule(new PassThroughRule(modelInput, modelOutput, false));
    }

    /**
     * Applies the operator and labels the {@link ExampleSet}. The example set in the input is not
     * consumed.
     */
    @Override
    public void doWork() throws OperatorException {
//        ExampleSet inputExampleSet = exampleSetInput.getData(ExampleSet.class);
//        Model model = modelInput.getData(Model.class);
//        if (AbstractModel.class.isAssignableFrom(model.getClass())) {
//            ((AbstractModel) model).setOperator(this);
//            ((AbstractModel) model).setShowProgress(true);
//        }
//
//        List<String[]> modelParameters = getParameterList(PARAMETER_APPLICATION_PARAMETERS);
//        Iterator<String[]> i = modelParameters.iterator();
//        while (i.hasNext()) {
//            String[] parameter = i.next();
//            try {
//                model.setParameter(parameter[0], parameter[1]);
//            } catch (UnsupportedApplicationParameterError e) {
//                if (getCompatibilityLevel().isAtMost(VERSION_ERROR_UNSUPPORTED_PARAMETER)) {
//                    log("The learned model does not support parameter");
//                } else {
//                    e.setOperator(this);
//                    throw e;
//                }
//            }
//        }
//
//        // handling PreprocessingModels: extra treatment for views
//        if (getParameterAsBoolean(PARAMETER_CREATE_VIEW)) {
//            try {
//                model.setParameter(PreprocessingOperator.PARAMETER_CREATE_VIEW, true);
//            } catch (UnsupportedApplicationParameterError e) {
//                if (getCompatibilityLevel().isAtMost(VERSION_ERROR_UNSUPPORTED_PARAMETER)) {
//                    log("The learned model does not have a view to create");
//                } else {
//                    e.setOperator(this);
//                    throw e;
//                }
//            }
//        }
//
//        ExampleSet result = inputExampleSet;
//        try {
//            result = model.apply(inputExampleSet);
//        } catch (UserError e) {
//            if (e.getOperator() == null) {
//                e.setOperator(this);
//            }
//            throw e;
//        }
//
//        if (AbstractModel.class.isAssignableFrom(model.getClass())) {
//            ((AbstractModel) model).setOperator(null);
//            ((AbstractModel) model).setShowProgress(false);
//        }
//
//        exampleSetOutput.deliver(result);
//        modelOutput.deliver(model);
    }

//    @Override
//    public boolean shouldAutoConnect(OutputPort port) {
//        if (port == modelOutput) {
//            return getParameterAsBoolean("keep_model");
//        } else {
//            return super.shouldAutoConnect(port);
//        }
//    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeList(PARAMETER_APPLICATION_PARAMETERS,
                "Model parameters for application (usually not needed).",
                new ParameterTypeString(PARAMETER_KEY, "The model parameter key."),
                new ParameterTypeString(PARAMETER_VALUE, "This key's value")));
        types.add(new ParameterTypeBoolean(PARAMETER_CREATE_VIEW,
                "Indicates that models should create a new view on the data where possible. Then, instead of changing the data itself, the results are calculated on the fly if needed.",
                false));
        return types;
    }
}
