package io.sugo.pio.dl4j.modeling.prediction;

import io.sugo.pio.dl4j.io.LayerSemaphore;
import io.sugo.pio.dl4j.layers.AbstractLayer;
import io.sugo.pio.dl4j.layers.OutputLayer;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 */
public abstract class AbstractDLModelLearner extends OperatorChain {

//    protected InputPort trainPort = getInputPorts().createPort("training examples", ExampleSet.class);
    protected InputPort trainPort = new InputPortImpl("training examples");
//    protected OutputPort modelPort = getOutputPorts().createPort("model");
    protected OutputPort modelPort = new OutputPortImpl("model");
//    protected OutputPort examplePort = getOutputPorts().createPort("examples");
    protected OutputPort examplePort = new OutputPortImpl("examples");
//    protected final OutputPort start = getSubprocess(0).getInnerSources().createPort("start");
    protected final OutputPort start = new OutputPortImpl("start");
//    protected final InputPort end = getSubprocess(0).getInnerSinks().createPort("end");
    protected final InputPort end = new InputPortImpl("end");

    protected List<AbstractLayer> structure = new LinkedList<AbstractLayer>();

    /**
     * The parameter name for &quot;The number of training iterations used for the training.&quot;
     */
    public static final String PARAMETER_ITERATION = "iteration";

    /**
     * The parameter name for &quot;The learning rate determines by how much we change the weights
     * at each step.&quot;
     */
    public static final String PARAMETER_LEARNING_RATE = "learning_rate";

    /**
     * The parameter name for &quot;The rate that learning rate decreases.&quot;
     */
    public static final String PARAMETER_DECAY = "decay";

    /**
     * The parameter name for &quot;The momentum simply adds a fraction of the previous weight
     * update to the current one (prevent local maximum and smoothes optimization directions).&quot;
     */
    public static final String PARAMETER_MOMENTUM = "momentum";

    /**
     * The parameter name for &quot;The optimization algorithm of the neural net.&quot;
     */
    public static final String PARAMETER_OPTIMIZATION_ALGORITHM = "optimiazation_algorithm";

    /**
     * The category &quot;optimize algorithm&quot;
     */
    public static final String[] OPTIMIZE_ALGORITHM_NAMES = new String[]{
            "line_gradient_descent"
            ,"conjugate_gradient"
            ,"lbfgs"
            ,"stochastic_gradient_descent"
    };

    /**
     * Indicates if the input data should be shuffled before learning.
     */
    public static final String PARAMETER_SHUFFLE = "shuffle";

    /**
     * Indicates if the input data should be normalized between -1 and 1 before learning.
     */
    public static final String PARAMETER_NORMALIZE = "normalize";

    /**
     * Indicate if to use regularization
     */
    public static final String PARAMETER_REGULARIZATION = "regularization";

    /**
     * The name for &quot;The weight of l1 regularization.&quot;
     */
    public static final String PARAMETER_L1 = "l1";

    /**
     * The name for &quot;The weight of l2 regularization.&quot;
     */
    public static final String PARAMETER_L2 = "l2";

    /**
     * Indicates if to use mini batch.
     */
    public static final String PARAMETER_MINIBATCH = "mini_batch";

    /**
     * Indicates if to minimize the loss function or maximize.
     */
    public static final String PARAMETER_MINIMIZE = "minimize_loss_function";

    /**
     * Indicates if to use local random seed.
     */
    public static final String PARAMETER_USE_LOCAL_RANDOM_SEED = "use_local_random_seed";

    /**
     * The name for &quot;The value of local random seed.&quot;
     */
    public static final String PARAMETER_LOCAL_RANDOM_SEED = "local_random_seed";


    public AbstractDLModelLearner() {
        super("Layer Structure", null, null);
        addInputPort(trainPort);
        addOutputPort(modelPort);
        addOutputPort(examplePort);
        addOutputPort(start);
        addInputPort(end);
    }

    @Override
    public void doWork() {
        start.deliver(new LayerSemaphore("0"));
        List<Operator> list = getSubprocess(0).getOperators();
        structure = convertStructure(getStructure(list));

        // validate the input examples
        ExampleSet exampleSet = trainPort.getData(ExampleSet.class);
        Model model = learn(exampleSet);
        modelPort.deliver(model);

        examplePort.deliver(exampleSet);
        super.doWork();
    }

    abstract public Model learn(ExampleSet exampleSet);

    protected List<Operator> getStructure(List<Operator> list) {
        List<Operator> result = new LinkedList<Operator>();
        for (Operator operator : list){
            if (AbstractLayer.class.isAssignableFrom(operator.getClass())){
                if (((AbstractLayer)operator).isLinked()){
                    result.add(operator);
                }
                if (operator.getClass() == OutputLayer.class){
                    return result;
                }

            } else {
                throw new RuntimeException("Invalid operaoter nested in " + getName() +"; only layers allowed");
            }
        }

        return result;
    }

    protected List<AbstractLayer> convertStructure(List<Operator> list) {
        List<AbstractLayer> result = new LinkedList<>();
        for (int i=0;i<list.size();i++){
            result.add((AbstractLayer)list.get(i));
        }
        return result;
    }

    protected OptimizationAlgorithm getOptimizationAlgorithm(int i){
        switch (i) {
            case 0 :
                return OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
            case 1 :
                return OptimizationAlgorithm.CONJUGATE_GRADIENT;
            case 2 :
                return OptimizationAlgorithm.LBFGS;
            case 3 :
                return OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
            default :
                return null;
        }
    }

    /**
     * Parameters allow for customization
     */
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = null;

        // general setting
        types.add(new ParameterTypeInt(
                PARAMETER_ITERATION,
                "The number of iterations used for the neural network training.",
                1, Integer.MAX_VALUE, 500));

        types.add(new ParameterTypeDouble(
                PARAMETER_LEARNING_RATE,
                "The learning rate determines by how much we change the weights at each step. May not be 0.",
                Double.MIN_VALUE, 1.0d, 0.9));

        types.add(new ParameterTypeDouble(
                PARAMETER_DECAY,
                "The rate that learning rate decreases",
                0.0d, 1.0d, 0.99d));

        types.add(new ParameterTypeDouble(
                PARAMETER_MOMENTUM,
                "The momentum simply adds a fraction of the previous weight update to the current one (prevent local maxima and smoothes optimization directions).",
                0.0d, 1.0d, 0.2d));

        types.add(new ParameterTypeCategory(
                PARAMETER_OPTIMIZATION_ALGORITHM,
                "The opimization function",
                OPTIMIZE_ALGORITHM_NAMES,
                1));

        // for expert features
        type = new ParameterTypeBoolean(
                PARAMETER_SHUFFLE,
                "Indicates if the input data should be shuffled before learning.",
                true);
        types.add(type);

        type = new ParameterTypeBoolean(
                PARAMETER_NORMALIZE,
                "Indicates if the input data should be normalized.",
                true);
        types.add(type);

        type = new ParameterTypeBoolean(
                PARAMETER_REGULARIZATION,
                "Indicates if to use regularization. This prevent overfitting and balance weights between features",
                false);
        types.add(type);

        type = new ParameterTypeDouble(
                PARAMETER_L1,
                "The weight on l1 regularization.",
                0d, 1d, 0d);
        type.registerDependencyCondition(
                new BooleanParameterCondition(
                        this,
                        PARAMETER_REGULARIZATION,
                        false,true));
        types.add(type);

        type = new ParameterTypeDouble(
                PARAMETER_L2,
                "The weight on l2 regularization.",
                0d, 1d, 0d);
        type.registerDependencyCondition(
                new BooleanParameterCondition(
                        this,
                        PARAMETER_REGULARIZATION,
                        false,true));
        types.add(type);

        type = new ParameterTypeBoolean(
                PARAMETER_MINIBATCH,
                "Indicates if to use miniBatch.",
                true);
        types.add(type);

        type = new ParameterTypeBoolean(
                PARAMETER_MINIMIZE,
                "Indicates if to minimize or maximize the loss function.",
                true);
        types.add(type);

        type = new ParameterTypeBoolean(
                PARAMETER_USE_LOCAL_RANDOM_SEED,
                "Indicates if to set the value of random seed.",
                false);
        types.add(type);

        type = new ParameterTypeInt(
                PARAMETER_LOCAL_RANDOM_SEED,
                "The value of random seed",
                1, Integer.MAX_VALUE, 1992);

        type.registerDependencyCondition(
                new BooleanParameterCondition(
                        this,
                        PARAMETER_USE_LOCAL_RANDOM_SEED,
                        false,true));
        types.add(type);

        return types;
    }
}
