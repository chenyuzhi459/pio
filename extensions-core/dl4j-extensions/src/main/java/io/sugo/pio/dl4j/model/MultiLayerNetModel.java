package io.sugo.pio.dl4j.model;

import io.sugo.pio.dl4j.DL4JConvert;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.PredictionModel;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;

import java.util.ArrayList;
import java.util.List;

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
     * The list stores feature names.
     */
    List<String> featureNames;

    /**
     * The means of features of the training data, typically used for normalization.
     */
    private INDArray columnMeans = null;

    /**
     * The standard deviation of features of the training data, typically used for normalization.
     */
    private INDArray columnStds = null;

    /**
     * The configuration information of the mutilayer network model.
     */
    private MultiLayerConfiguration configuration;

    public MultiLayerNetModel(ExampleSet exampleSet) {
        super(exampleSet,ExampleSetUtilities.SetsCompareOption.ALLOW_SUPERSET, ExampleSetUtilities.TypesCompareOption.ALLOW_SAME_PARENTS);

        // record the attributes
        Attributes attributes = exampleSet.getAttributes();
        featureNames = new ArrayList<String>();
        for (Attribute attribute : attributes){
            featureNames.add(attribute.getName());
        }

        model = null;
        configuration = null;
    }

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

    /**
     * Retrieve the nominal mapping that maps the label names to numerics.
     * @return the nominal mapping
     */
    public NominalMapping getLabelMapping(){
        return getLabel().getMapping();
    }

    /**
     * Retrieve the names of labels, i.e. the possible values of the label attributes.
     * @return a list of the label names
     */
    public List<String> getLabelName(){
        return getLabelMapping().getValues();
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        if (exampleSet.getAttributes().getPredictedLabel() != predictedLabel){
            exampleSet.getAttributes().setPredictedLabel(predictedLabel);
        }

        // construct the table of features
        int feature_num = featureNames.size();
        int row_num = exampleSet.size();
        double[][] featuresMatrix = new double[row_num][feature_num];

        int counter = 0;
        for (Example e : exampleSet){
            for (int i=0; i<feature_num; i++){
                double d = e.getValue(exampleSet.getAttributes().get(featureNames.get(i)));
                featuresMatrix[counter][i] = d;
            }
            counter++;
        }

        // build the 2d-array of the features
        INDArray features = org.nd4j.linalg.factory.Nd4j.create(featuresMatrix);

        // normalize features in the same way that the training data is normalized.
        if (columnMeans != null && columnStds != null){
            features = features.subiRowVector(columnMeans);
            features = features.diviRowVector(columnStds);
        }

        // make prediction
        INDArray output = model.output(features);

		/*
		 * convert the output 2d-array to the specified attribute in the exampleset,
		 * together with the confidences on each label
		 */
        int[] indices = DL4JConvert.getMax(output);
        NominalMapping mapping = getLabelMapping();
        int numLabel = mapping.getValues().size();

        counter = 0;
        for (Example e : exampleSet){

            e.setPredictedLabel(indices[counter]);

            for (int i=0; i<numLabel; i++){
                e.setConfidence(mapping.mapIndex(i), output.getRow(counter).getDouble(i));
            }
            counter++;
        }

        return exampleSet;
    }
}
