package io.sugo.pio.engine.training;

import com.google.common.base.Preconditions;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple3;

import java.util.List;

/**
 */
public abstract class Engine<TD, PD, MD, EQ, EAR, IND> {
    private final EngineParams engineParams;

    public Engine(EngineParams engineParams) {
        Preconditions.checkNotNull(engineParams, "EngineParams should not be null");

        this.engineParams = engineParams;
    }

    protected abstract DataSource<TD, EQ, EAR> createDatasource(Params params);

    protected abstract Preparator<TD, PD> createPreparator(Params params);

    protected abstract Algorithm<PD, MD> createAlgorithm(Params params);

    protected abstract Model<MD> createModel();

    protected abstract Evalution<MD, EQ, EAR, IND> createEval();

    public void train(JavaSparkContext sc) {
        DataSource<TD, EQ, EAR> dataSource = createDatasource(engineParams.getDatasourceParams());
        Preparator<TD, PD> preparator = createPreparator(engineParams.getPreparatorParams());
        Model<MD> model = createModel();
        Algorithm<PD, MD> alg = createAlgorithm(engineParams.getAlgorithmParams());

        TD trainingData = dataSource.readTraining(sc);
        PD preparedData = preparator.prepare(sc, trainingData);
        MD modelData = alg.train(sc, preparedData);
        model.save(modelData);
    }

    public void eval(JavaSparkContext sc) {
        DataSource<TD, EQ, EAR> dataSource = createDatasource(engineParams.getDatasourceParams());
        Preparator<TD, PD> preparator = createPreparator(engineParams.getPreparatorParams());
        Algorithm<PD, MD> alg = createAlgorithm(engineParams.getAlgorithmParams());
        Evalution<MD, EQ, EAR, IND> evalution = createEval();
        List<Tuple3<TD, EQ, EAR>> evalDataList = dataSource.readEval(sc);

        for(Tuple3<TD, EQ, EAR> evalData: evalDataList) {
            TD trainingData = evalData._1();
            EQ evalQuery = evalData._2();
            EAR evalActualResult = evalData._3();
            PD preparedData = preparator.prepare(sc, trainingData);
            MD modelData = alg.train(sc, preparedData);
            IND ind = evalution.predict(modelData, evalQuery, evalActualResult);
        }
    }
}
