package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.hdfs.TempHDFSDirectory;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkJobResult;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.spark.modeling.prediction.AbstractLearner;
import io.sugo.pio.spark.operator.spark.SparkTools.SparkFinalState;
import io.sugo.pio.spark.transfer.model.ModelTransferObject;
import io.sugo.pio.spark.transfer.parameter.CommonParameter;
import io.sugo.pio.spark.transfer.parameter.SparkParameter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;

/**
 */
public abstract class AbstractSparkLearner<T extends ModelTransferObject, M extends PredictionModel> extends AbstractLearner {
    public AbstractSparkLearner(SparkOperation sparkOperation, String name,
                                InputPort exampleSetInput, OutputPort modelOutput) {
        super(sparkOperation, name, exampleSetInput, modelOutput);
    }

    @Override
    public PredictionModel learn(HadoopExampleSet inputHes) {
        TempHDFSDirectory sparkOutputDirectory = new TempHDFSDirectory(getSparkNest());
        SparkParameter commonParams = setupCommonParams(inputHes, sparkOutputDirectory);
        SparkParameter params = setupAlgorithmParams(inputHes);
        SparkJobResult result = getMapReduceHDFSHandler().runSpark(this, sparkOperation, Collections.emptyList(), commonParams, params);
        SparkFinalState finalState  = result.getFinalState();
        M model;
        try {
            model = getModel(inputHes, sparkOutputDirectory, finalState);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert Spark model to RapidMiner model!", e);
        }
        postProcessModel(model, inputHes);
        return model;
    }

    protected abstract void postProcessModel(M model, HadoopExampleSet exampleSet);

    protected abstract SparkParameter setupAlgorithmParams(HadoopExampleSet inputHes);

    protected SparkParameter setupCommonParams(HadoopExampleSet inputHes, TempHDFSDirectory sparkOutputDirectory) {
        SparkParameter commonParams = new CommonParameter();
        return commonParams;
    }

    protected M getModel(HadoopExampleSet inputHes, TempHDFSDirectory sparkOutputDirectory, SparkFinalState finalState) throws IOException {
        MapReduceHDFSHandler handler = getMapReduceHDFSHandler();
//        HDFSDirectoryReader reader = handler.new HDFSDirectoryReader(sparkOutputDirectory.getSubDir());

        if(!finalState.equals(SparkFinalState.SUCCEEDED)) {
            return null;
        } else {
//            StringBuffer e = new StringBuffer();
//
//            String row;
//            while((row = reader.readLineFromDirectory()) != null) {
//                e.append(row);
//            }
//
//            T mto = (T) TransferObject.fromJson(e.toString(), getMTOClass());
//            MTOConverter.setNominalMappings(mto, inputHes);
//            return convertModelFromMTO(mto, inputHes);
            return null;
        }
    }

    protected abstract M convertModelFromMTO(T mto, HadoopExampleSet exampleSet);

    private Class<T> getMTOClass() {
        return (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
}
