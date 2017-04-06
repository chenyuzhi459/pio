package io.sugo.pio.ffm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
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

    @JsonProperty
    private boolean normalization;

    @JsonProperty
    private String firstClassName;

    @JsonProperty
    private String secondClassName;

    /**
     */
    protected FieldAwareFactorizationMachineModel(ExampleSet exampleSet,
                                                  int featureNum,
                                                  int fieldNum,
                                                  int latentFactorDim,
                                                  float[] weights,
                                                  boolean normalization,
                                                  String firstClassName,
                                                  String secondClassName) {
        super(exampleSet, null, null);
        this.featureNum = featureNum;
        this.fieldNum = fieldNum;
        this.latentFactorDim = latentFactorDim;
        this.weights = weights;
        this.normalization = normalization;
        this.firstClassName = firstClassName;
        this.secondClassName = secondClassName;
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        FFMModel ffmModel = new FFMModel(featureNum, fieldNum, latentFactorDim, weights, normalization);
        FFMProblem predictProblem = FFMProblem.convertExampleSet(exampleSet);
        float[] yLabels = ffmModel.predict(ffmModel, predictProblem);

        int index = 0;
        for (Example example : exampleSet) {
            float y = yLabels[index];
            if (predictedLabel.isNominal()) {
                int predictionIndex = y> 0.5 ? predictedLabel.getMapping().getIndex(secondClassName)
                        : predictedLabel.getMapping().getIndex(firstClassName);
                example.setValue(predictedLabel, predictionIndex);

//                double logFunction = 1.0d / (1.0d + Math.exp(-(prediction - 0.5)));
//                example.setConfidence(secondClassName, logFunction);
//                example.setConfidence(firstClassName, 1 - logFunction);
            } else {
                example.setValue(predictedLabel, y);
            }

            index++;
        }

        return exampleSet;
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

    public boolean isNormalization() {
        return normalization;
    }
}
