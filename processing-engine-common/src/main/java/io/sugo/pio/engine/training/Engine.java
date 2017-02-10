package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public abstract class Engine<TD, PD, MD> {
    private final EngineParams engineParams;

    public Engine(EngineParams engineParams) {
        this.engineParams = engineParams;
    }

    protected abstract DataSource<TD> createDatasource(Params params);

    protected abstract Preparator<TD, PD> createPreparator(Params params);

    protected abstract Algorithm<PD, MD> createAlgorithm(Params params);

    protected abstract Model<MD> createModel();

    public void train(JavaSparkContext sc) {
        DataSource<TD> dataSource = createDatasource(engineParams.getDatasourceParams());
        Preparator<TD, PD> preparator = createPreparator(engineParams.getPreparatorParams());
        Model<MD> model = createModel();
        Algorithm<PD, MD> alg = createAlgorithm(engineParams.getAlgorithmParams());

        TD trainingData = dataSource.readTraining(sc);
        PD preparedData = preparator.prepare(sc, trainingData);
        MD modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }

    public void eval(JavaSparkContext sc) {
        DataSource<TD> dataSource = createDatasource(engineParams.getDatasourceParams());
        Preparator<TD, PD> preparator = createPreparator(engineParams.getPreparatorParams());
        Model<MD> model = createModel();
        Algorithm<PD, MD> alg = createAlgorithm(engineParams.getAlgorithmParams());

        TD trainingData = dataSource.readTraining(sc);
        PD preparedData = preparator.prepare(sc, trainingData);
        MD modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }
}
