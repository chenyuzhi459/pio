package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public abstract class Engine<TD, PD, MD> {
    protected abstract DataSource<TD> createDatasource();

    protected abstract Preparator<TD, PD> createPreparator();

    protected abstract Algorithm<PD, MD> createAlgorithm();

    protected abstract Model<MD> createModel();

    public void train(JavaSparkContext sc) {
        DataSource<TD> dataSource = createDatasource();
        Preparator<TD, PD> preparator = createPreparator();
        Model<MD> model = createModel();
        Algorithm<PD, MD> alg = createAlgorithm();

        TD trainingData = dataSource.readTraining(sc);
        PD preparedData = preparator.prepare(sc, trainingData);
        MD modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }

    public void eval(JavaSparkContext sc) {
        DataSource<TD> dataSource = createDatasource();
        Preparator<TD, PD> preparator = createPreparator();
        Model<MD> model = createModel();
        Algorithm<PD, MD> alg = createAlgorithm();

        TD trainingData = dataSource.readTraining(sc);
        PD preparedData = preparator.prepare(sc, trainingData);
        MD modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }
}
