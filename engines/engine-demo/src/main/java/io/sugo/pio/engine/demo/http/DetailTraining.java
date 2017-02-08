package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.demo.Constants;
import io.sugo.pio.engine.demo.FileUtil;
import io.sugo.pio.engine.demo.data.MovieBatchEventHose;
import io.sugo.pio.engine.detail.DetailEngineFactory;
import io.sugo.pio.engine.detail.data.DetailModelData;
import io.sugo.pio.engine.detail.data.DetailPreparedData;
import io.sugo.pio.engine.detail.data.DetailTrainingData;
import io.sugo.pio.engine.training.Algorithm;
import io.sugo.pio.engine.training.DataSource;
import io.sugo.pio.engine.training.Model;
import io.sugo.pio.engine.training.Preparator;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;

/**
 */
public class DetailTraining extends AbstractTraining {
    @Override
    protected JavaSparkContext init() {
        SparkConf sparkConf = new SparkConf().setMaster("local[1]").setAppName("test");
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.set("spark.kryo.registrator", "org.apache.mahout.sparkbindings.io.MahoutKryoRegistrator");
        sparkConf.set("spark.kryo.referenceTracking", "false");
        sparkConf.set("spark.kryoserializer.buffer", "300m");
        return new JavaSparkContext(sparkConf);
    }

    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        FileUtil.deleteDirectory(new File(DetailResource.REPOSITORY_PATH));
        BatchEventHose eventHose = new MovieBatchEventHose(Constants.DATA_PATH, Constants.DATA_SEPERATOR);
        Repository repository = new LocalFileRepository(DetailResource.REPOSITORY_PATH);
        DetailEngineFactory engineFactory = new DetailEngineFactory(eventHose, repository);
        Preparator<DetailTrainingData, DetailPreparedData> preparator = engineFactory.createPreparator();
        DataSource<DetailTrainingData> dataSource = engineFactory.createDatasource();
        DetailTrainingData trainingData = dataSource.readTraining(sc);
        DetailPreparedData preparedData = preparator.prepare(sc, trainingData);
        Algorithm<DetailPreparedData, DetailModelData> alg = engineFactory.createAlgorithm();
        DetailModelData modelData = alg.train(sc, preparedData);
        Model<DetailModelData> model = engineFactory.createModel();
        model.save(modelData);
    }
}