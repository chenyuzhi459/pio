package io.sugo.pio.dl4j.model;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.PredictionModel;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.DataSet;

/**
 * A multilayer network model.
 * This model functions as an interface between RM and DL4J;
 * it contains all information about the miltolayer network used in DL4J,
 * information may be duplicated for fast access.
 *
 * This class is still under development as some functions are not completely implemented,
 * with the current functions, the extension is able to work on certtain tasks.
 *
 * The information contained in this model are:
 * <ul>
 * 	<li>The multilayer network model itself;</li>
 * 	<li>The name of features;</li>
 * 	<li>The mean and standard deviation of the training data;</li>
 * 	<li>The configuration information of the multilayer network model;</li>
 * 	<li>The transfer matrices (weights) between each two layers</li>
 * </ul>
 *
 * Currently:
 * <ul>
 * 	<li>The transfer matrices are not implemented, as they are not used;</li>
 * 	<li>The method clone is not implemented, as they are not used in current implementation.</li>
 * </ul>
 *
 * @author Anson Chen
 * @version 0.3.1
 */
public class MultiLayerNetModel extends PredictionModel {
    /**
     * The multilayer network model.
     */
    private MultiLayerNetwork model;

    /**
     * The configuration information of the mutilayer network model.
     */
    private MultiLayerConfiguration configuration;

    /**
     * Train the data.
     *
     * @param exampleSet the training exampleset
     * @param shuffle whether to shuffle the examples;
     *        I may not include this boolean in later version as RM has its own shuffle method in split operator
     * @param normalization whether to normalize each column
     */
    public void train(ExampleSet exampleSet, boolean shuffle, boolean normalization){
        this.model = new MultiLayerNetwork(configuration);
        model.init();
        DataSet data = null;

        // train the model
        model.fit(data);
    }

    public MultiLayerNetModel clone(){
        return null;
    }
}
