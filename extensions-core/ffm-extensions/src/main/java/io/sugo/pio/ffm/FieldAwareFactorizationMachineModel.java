package io.sugo.pio.ffm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.PredictionModel;

/**
 */
public class FieldAwareFactorizationMachineModel extends PredictionModel {
    // max(feature_num) + 1
    @JsonProperty
    private int featureNum;

    // max(field_num) + 1
    @JsonProperty
    private int fieldNum;

    // latent factor dim
    @JsonProperty
    private int latentFactorDim;

    // length = n * m * k * 2
    @JsonProperty
    private float[] weights;

    /**
     */
    protected FieldAwareFactorizationMachineModel(ExampleSet exampleSet,
                                                  int featureNum,
                                                  int fieldNum,
                                                  int latentFactorDim,
                                                  float[] weights) {
        super(exampleSet, null, null);
        this.featureNum = featureNum;
        this.fieldNum = fieldNum;
        this.latentFactorDim = latentFactorDim;
        this.weights = weights;
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        return null;
    }

    public int getFeatureNum() {
        return featureNum;
    }

    public int getFieldNum() {
        return fieldNum;
    }

    public int getLatentFactorDim() {
        return latentFactorDim;
    }

    public float[] getWeights() {
        return weights;
    }
}
