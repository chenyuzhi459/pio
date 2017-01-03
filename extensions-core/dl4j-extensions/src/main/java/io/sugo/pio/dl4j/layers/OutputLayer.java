package io.sugo.pio.dl4j.layers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.List;

/**
 */
public class OutputLayer extends AbstractLayer {
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
     * The parameter name for &quot;Loss function for this layer.&quot;
     */
    public static final String PARAMETER_LOSS_FUNCTION = "loss_function";

    /**
     * The category &quot;Loss function&quot;
     */
    public static final String[] LOSS_FUNCTION_NAMES = new String[]{
            "mean squared error"
            ,"exponential log likelihood"
            ,"cross Entropy"
            ,"multiclass cross entropy"
            ,"RMSE cross entropy"
            ,"squared Loss"
            ,"reconstruction cross entropy"
            ,"negative log likelihood"
    };

    private int numNodes;

    @JsonCreator
    public OutputLayer(
            @JsonProperty("name") String name
    ) {
        super(name);
    }

    @Override
    public Layer getLayer() {
        return generateBuilder().build();
    }

    @Override
    public Layer getLayer(int i) {
        org.deeplearning4j.nn.conf.layers.OutputLayer.Builder builder =
                generateBuilder().nIn(i);
        return builder.build();
    }

    public org.deeplearning4j.nn.conf.layers.OutputLayer.Builder generateBuilder() {
        String name = getParameterAsString(PARAMETER_NAME);

        numNodes = getParameterAsInt(PARAMETER_NUMEBR_OF_NODE);
        String activation = getParameterAsString(PARAMETER_ACTIVATION_FUNCTION);

        int lossIndex = getParameterAsInt(PARAMETER_LOSS_FUNCTION);
        LossFunctions.LossFunction loss = getLossFunction(lossIndex);

        org.deeplearning4j.nn.conf.layers.OutputLayer.Builder builder =
                new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(loss)
                        .name(name)
                        .nOut(numNodes)
                        .activation(activation)
                        .lossFunction(loss)
                        .weightInit(WeightInit.XAVIER);

        return builder;
    }

    /**
     * This method will be mostly used
     */
    public Layer getLayer(int in, int out) {
        org.deeplearning4j.nn.conf.layers.OutputLayer.Builder builder =
                generateBuilder()
                        .nIn(in)
                        .nOut(out);
        return builder.build();
    }

    private LossFunctions.LossFunction getLossFunction(int i){
        switch (i) {
            case 0:
                return LossFunctions.LossFunction.MSE;
            case 1:
                return LossFunctions.LossFunction.EXPLL;
            case 2:
                return LossFunctions.LossFunction.XENT;
            case 3:
                return LossFunctions.LossFunction.MCXENT;
            case 4:
                return LossFunctions.LossFunction.RMSE_XENT;
            case 5:
                return LossFunctions.LossFunction.SQUARED_LOSS;
            case 6:
                return LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY;
            case 7:
                return LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
            default:
                return null;
        }
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
                "Output Layer"
        ));

        type = new ParameterTypeInt(PARAMETER_NUMEBR_OF_NODE,
                "The number of nodes in this layer",
                1,Integer.MAX_VALUE,10
        );
        types.add(type);

        types.add(new ParameterTypeCategory(PARAMETER_ACTIVATION_FUNCTION,
                "The activation function of this layer",
                ACTIVATION_FUNCTION_NAMES,
                3));

        types.add(new ParameterTypeCategory(PARAMETER_LOSS_FUNCTION,
                "The loss function of this layer",
                LOSS_FUNCTION_NAMES,
                0));

        return types;
    }
}
