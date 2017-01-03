package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.parameter.*;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.hdfs.TempHDFSDirectory;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.spark.modeling.prediction.AbstractLearner;
import io.sugo.pio.spark.transfer.model.ModelTransferObject;
import io.sugo.pio.spark.transfer.parameter.CommonParameter;
import io.sugo.pio.spark.transfer.parameter.SparkParameter;

import java.io.IOException;
import java.util.List;

/**
 */
public class SparkCustomEngineLearner<T extends ModelTransferObject, M extends PredictionModel> extends AbstractLearner {
    public SparkCustomEngineLearner(OperatorDescription description, MapReduceHDFSHandler.SparkOperation sparkOperation) {
        super(description, sparkOperation);
    }

    @Override
    public PredictionModel learn(HadoopExampleSet inputHes) {
        TempHDFSDirectory sparkOutputDirectory = new TempHDFSDirectory(getSparkNest());
        SparkParameter commonParams = setupCommonParams();
        SparkParameter params = setupAlgorithmParams(inputHes);
        MapReduceHDFSHandler.SparkJobResult result = getMapReduceHDFSHandler().runSpark( this, sparkOperation, commonParams, params);
        SparkTools.SparkFinalState finalState = result.getFinalState();
        M model;
        try {
            model = getModel(inputHes, sparkOutputDirectory, finalState);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert Spark model to RapidMiner model!", e);
        }
        postProcessModel(model, inputHes);
        return model;
    }

    @Override
    public boolean canEstimatePerformance() {
        return false;
    }

    @Override
    public boolean canCalculateWeights() {
        return false;
    }

    private void postProcessModel(M model, HadoopExampleSet exampleSet) {
    }

    private SparkParameter setupAlgorithmParams(HadoopExampleSet inputHes){
        return null;
    }

    protected SparkParameter setupCommonParams() {
        SparkParameter commonParams = new CommonParameter();
        return commonParams;
    }

    protected M getModel(HadoopExampleSet inputHes, TempHDFSDirectory sparkOutputDirectory, SparkTools.SparkFinalState finalState) throws IOException {
        MapReduceHDFSHandler handler = getMapReduceHDFSHandler();

        if(!finalState.equals(SparkTools.SparkFinalState.SUCCEEDED)) {
            return null;
        } else {
            return null;
        }
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}