package io.sugo.pio.dl4j.layers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.weights.WeightInit;

import java.util.List;

/**
 */
public class DenseLayer extends AbstractLayer {
    /**
     * The parameter name for &quot;Name of this layer.&quot;
     */
    public static final String PARAMETER_NAME = "name";

    /**
     * The parameter name for &quot;Number of nodes of this layer.&quot;
     */
    public static final String PARAMETER_NUMEBR_OF_NODE = "number_of_nodes";

    /**
     * The parameter name for &quot;Activation function for this layer.&quot;
     */
    public static final String PARAMETER_ACTIVATION_FUNCTION = "activation_function";

    /**
     * The category &quot;Activation function&quot;
     */
    public static final String[] ACTIVATION_FUNCTION_NAMES = new String[]{
            "relu"
            ,"tanh"
            ,"sigmoid"
            ,"softmax"
            ,"hardtanh"
            ,"leakyrelu"
            ,"maxout"
            ,"softsign"
            ,"softplus"
    };

    /**
     * Indicates if to use dropout.
     */
    public static final String PARAMETER_DROPOUT = "dropout";

    /**
     * The parameter name for &quot;Dropout rate.&quot;
     */
    public static final String PARAMETER_DROPOUT_RATE = "dropout_rate";

    /**
     * The parameter name for &quot;Updater for each hidden layer.&quot;
     */
    public static final String PARAMETER_UPDATER = "updater";

    /**
     * The category &quot;Updater&quot;
     */
    public static final String[] UPDATER_NAMES = new String[]{
            "SGD"
            ,"ADAM"
            ,"ADADelta"
            ,"Nesterovs"
            ,"ADAGrad"
            ,"RMSProp"
            ,"none"
    };

    private int numNodes;


    @JsonCreator
    public DenseLayer(
            @JsonProperty("name") String name
    ) {
        super(name);
    }

    public org.deeplearning4j.nn.conf.layers.DenseLayer.Builder generateBuilder() {

        String name = getParameterAsString(PARAMETER_NAME);
        numNodes = getParameterAsInt(PARAMETER_NUMEBR_OF_NODE);

        String activation = getParameterAsString(PARAMETER_ACTIVATION_FUNCTION);

        boolean dropout = getParameterAsBoolean(PARAMETER_DROPOUT);
        double dropoutrate = getParameterAsDouble(PARAMETER_DROPOUT_RATE);

        int updaterIndex = getParameterAsInt(PARAMETER_UPDATER);
        Updater updater = getUpdater(updaterIndex);


        org.deeplearning4j.nn.conf.layers.DenseLayer.Builder builder =
                new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder()
                        .name(name)
                        .nOut(numNodes)
                        .activation(activation)
                        .updater(updater)
                        .weightInit(WeightInit.XAVIER);

        if (dropout){
            builder = builder.dropOut(dropoutrate);
        }

        return builder;
    }

    @Override
    public Layer getLayer() {
        return generateBuilder().build();
    }

    @Override
    public Layer getLayer(int i) {
        org.deeplearning4j.nn.conf.layers.DenseLayer.Builder builder = generateBuilder().nIn(i);
        return builder.build();
    }

    @Override
    public int getNumNodes() {
        return numNodes;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = null;
        types.add(new ParameterTypeString(PARAMETER_NAME,
                "The name of this layer",
                "Dense Layer"
        ));

        types.add(new ParameterTypeInt(PARAMETER_NUMEBR_OF_NODE,
                "The number of nodes in this layer",
                1,Integer.MAX_VALUE,10
        ));

        types.add(new ParameterTypeCategory(PARAMETER_ACTIVATION_FUNCTION,
                "The activation function of this layer",
                ACTIVATION_FUNCTION_NAMES,
                2));

        type = new ParameterTypeBoolean(PARAMETER_DROPOUT,
                "Indicates if to use dropout, using dropout helps overcome overfitting, but may disturb converge for very small network",
                false);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_DROPOUT_RATE,
                "The dropout rate for this layers.",
                0d,0.7d,0.5d);
        type.registerDependencyCondition(
                new BooleanParameterCondition(this,
                        PARAMETER_DROPOUT,
                        false, true));
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_UPDATER,
                "The updater for this layer",
                UPDATER_NAMES,
                0);
        types.add(type);

        return types;
    }

    private Updater getUpdater(int i){
        switch (i) {
            case 0:
                return Updater.SGD;
            case 1:
                return Updater.ADAM;
            case 2:
                return Updater.ADADELTA;
            case 3:
                return Updater.NESTEROVS;
            case 4:
                return Updater.ADAGRAD;
            case 5:
                return Updater.RMSPROP;
            case 6:
                return Updater.NONE;
            default:
                return null;
        }
    }

}
