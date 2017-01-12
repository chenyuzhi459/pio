package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.demo.data.MoviePropertyHose;
import io.sugo.pio.engine.ur.detail.DetailEngineFactory;
import io.sugo.pio.engine.ur.detail.data.*;
import io.sugo.pio.spark.engine.Algorithm;
import io.sugo.pio.spark.engine.DataSource;
import io.sugo.pio.spark.engine.Model;
import io.sugo.pio.spark.engine.Preparator;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.input.PropertyHose;
import io.sugo.pio.spark.engine.data.output.LocalFileRepository;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class DetailTraining extends AbstractTraining {
    @Override
    protected JavaSparkContext init() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("test");
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.set("spark.kryo.registrator", "org.apache.mahout.sparkbindings.io.MahoutKryoRegistrator");
        sparkConf.set("spark.kryo.referenceTracking", "false");
        sparkConf.set("spark.kryoserializer.buffer", "300m");
        return new JavaSparkContext(sparkConf);
    }

    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtils.deleteDirectory(new File(DetailResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        PropertyHose propHose = new MoviePropertyHose(Constants.ITEM_PATH, Constants.ITEM_SEPERATOR, Constants.ITEM_GENS);
        DetailEngineFactory engineFactory = new DetailEngineFactory(propHose, eventHose);
        Preparator<DetailTrainingData, DetailPreparedData> preparator = engineFactory.createPreparator();
        DataSource<DetailTrainingData> dataSource = engineFactory.createDatasource();
        DetailTrainingData trainingData = dataSource.readTraining(sc);
        DetailPreparedData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<DetailPreparedData, DetailModelData> alg = engineFactory.createAlgorithm();
        DetailModelData modelData = alg.train(sc, preparedData);
        Model<DetailModelData, QueryableModelData> model = engineFactory.createModel();
        Repository repository = new LocalFileRepository(DetailResource.REPOSITORY_PATH);
        model.save(modelData, repository);
    }
}
