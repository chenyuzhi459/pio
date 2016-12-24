package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.hdfs.TempHDFSDirectory;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.HDFSDirectoryReader;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkJobResult;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.spark.modeling.prediction.AbstractLearner;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.spark.operator.spark.SparkTools.SparkFinalState;
import io.sugo.pio.spark.transfer.TransferObject;
import io.sugo.pio.spark.transfer.MTOConverter;
import io.sugo.pio.spark.transfer.model.ModelTransferObject;
import io.sugo.pio.spark.transfer.parameter.ParameterTransferObject.ParameterKey;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 */
public abstract class AbstractSparkLearner<P extends Enum<P> & ParameterKey, T extends ModelTransferObject, M extends PredictionModel> extends AbstractLearner {
    public AbstractSparkLearner(OperatorDescription description, SparkOperation sparkOperation) {
        super(description, sparkOperation);
    }

    @Override
    public PredictionModel learn(HadoopExampleSet inputHes) {
        SparkJobResult result = getMapReduceHDFSHandler().runSpark(this, this, sparkOperation);
        SparkFinalState finalState  = result.getFinalState();
        TempHDFSDirectory sparkOutputDirectory = new TempHDFSDirectory(getRadoopNest());
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

    protected M getModel(HadoopExampleSet inputHes, TempHDFSDirectory sparkOutputDirectory, SparkFinalState finalState) throws IOException {
        MapReduceHDFSHandler handler = getMapReduceHDFSHandler();
        HDFSDirectoryReader reader = handler.new HDFSDirectoryReader(sparkOutputDirectory.getSubDir());

        if(!finalState.equals(SparkFinalState.SUCCEEDED)) {
            return null;
        } else {
            StringBuffer e = new StringBuffer();

            String row;
            while((row = reader.readLineFromDirectory()) != null) {
                e.append(row);
            }

            T mto = (T) TransferObject.fromJson(e.toString(), getMTOClass());
            MTOConverter.setNominalMappings(mto, inputHes);
            return convertModelFromMTO(mto, inputHes);
        }
    }

    protected abstract M convertModelFromMTO(T mto, HadoopExampleSet exampleSet);

    private Class<T> getMTOClass() {
        return (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
}
