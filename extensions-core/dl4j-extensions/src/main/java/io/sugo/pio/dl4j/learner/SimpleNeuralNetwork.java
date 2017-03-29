package io.sugo.pio.dl4j.learner;

import io.sugo.pio.dl4j.layers.AbstractLayer;
import io.sugo.pio.dl4j.layers.ConvolutionalLayer;
import io.sugo.pio.dl4j.layers.OutputLayer;
import io.sugo.pio.dl4j.layers.SubSamplingLayer;
import io.sugo.pio.dl4j.model.MultiLayerNetModel;
import io.sugo.pio.dl4j.modeling.prediction.AbstractDLModelLearner;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.*;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SimpleNeuralNetwork extends AbstractDLModelLearner {

    @Override
    public String getDefaultFullName() {
        return SimpleNeuralNetwork.class.getSimpleName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.deepLearning;
    }

    @Override
    public String getDescription() {
        return SimpleNeuralNetwork.class.getSimpleName();
    }

    @Override
    public int getSequence() {
        return 0;
    }

    @Override
    public Model learn(ExampleSet exampleSet) throws OperatorException {
        MultiLayerNetModel model = new MultiLayerNetModel(exampleSet);

        // retrieve information
        // for the whole model

        // iteration
        int iteration = getParameterAsInt(PARAMETER_ITERATION);

        // learning rate, decay and momentum
        double learningRate = getParameterAsDouble(PARAMETER_LEARNING_RATE);
        double decay = getParameterAsDouble(PARAMETER_DECAY);
        double momentum = getParameterAsDouble(PARAMETER_MOMENTUM);

        // optimize function
        int optimizationAlgorithmIndex = getParameterAsInt(PARAMETER_OPTIMIZATION_ALGORITHM);
        OptimizationAlgorithm optimizationAlgorithm = getOptimizationAlgorithm(optimizationAlgorithmIndex);

        // for expert features
        // shuffle
        boolean shuffle = getParameterAsBoolean(PARAMETER_SHUFFLE);

        // normalize
        boolean normalize = getParameterAsBoolean(PARAMETER_NORMALIZE);

        // regularization
        boolean regularization = getParameterAsBoolean(PARAMETER_REGULARIZATION);
        double l1 = getParameterAsDouble(PARAMETER_L1);
        double l2 = getParameterAsDouble(PARAMETER_L2);

        // mimibatch
        boolean miniBatch = getParameterAsBoolean(PARAMETER_MINIBATCH);

        // minimize loss function
        boolean minimize = getParameterAsBoolean(PARAMETER_MINIMIZE);

        // seed
//		boolean specifySeed = getParameterAsBoolean(PARAMETER_USE_LOCAL_RANDOM_SEED);
        long seed = getParameterAsInt(PARAMETER_LOCAL_RANDOM_SEED);

        // set up the configurations
        NeuralNetConfiguration.Builder configBuilder = new NeuralNetConfiguration.Builder()
                .iterations(iteration)
                .learningRate(learningRate)
                .learningRateScoreBasedDecayRate(decay)
                .momentum(momentum)
                .optimizationAlgo(optimizationAlgorithm)
                .regularization(regularization)
                .miniBatch(miniBatch)
                .minimize(minimize)
                .seed(seed);

        if (regularization){
            configBuilder.setL1(l1);
            configBuilder.setL1(l2);
        }

        NeuralNetConfiguration.ListBuilder listBuilder = configBuilder.list();

        List<String> layerNames = new ArrayList<String>();
        int inSize = exampleSet.getAttributes().size();

        for (int i=0; i<structure.size(); i++){
            AbstractLayer layer = structure.get(i);

            if (i==structure.size()-1) {

                if(layer.getClass() == OutputLayer.class){

                    listBuilder.layer(i,((OutputLayer)layer).getLayer(inSize,
                            exampleSet.getAttributes().getLabel().getMapping().getValues().size()));
                    layerNames.add(layer.getLayerName());

                } else {
                    throw new OperatorException("Please put an output layer in the end of the neural network");
                }

            } else {

                if (layer.getClass() == ConvolutionalLayer.class
                        || layer.getClass() == SubSamplingLayer.class){
                    throw new OperatorException("Convolutional layers and subsampling layers are not "
                            + "supported in the General Neural Network: "
                            + this.getName() + ", please use Convolutional Neural Network, instead.");
                }


                listBuilder.layer(i,layer.getLayer(inSize));
                inSize = layer.getNumNodes();
                layerNames.add(layer.getLayerName());

            }
        }

        // construct the configuration information and train the model
        MultiLayerConfiguration config = listBuilder.build();
        model.train(exampleSet, config, shuffle, normalize, layerNames);
        return model;
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(modelPort.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }
}
