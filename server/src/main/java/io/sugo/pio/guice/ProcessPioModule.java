package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.constant.ProcessConstant;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.operator.ModelApplier;
import io.sugo.pio.operator.clustering.clusterer.KMeans;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.io.CSVExampleSetWriter;
import io.sugo.pio.operator.io.HttpSqlExampleSource;
import io.sugo.pio.operator.io.SingleViewExampleSource;
import io.sugo.pio.operator.learner.associations.AssociationRuleGenerator;
import io.sugo.pio.operator.learner.associations.fpgrowth.FPGrowth;
import io.sugo.pio.operator.learner.functions.kernel.JMySVMLearner;
import io.sugo.pio.operator.learner.functions.kernel.MyKLRLearner;
import io.sugo.pio.operator.learner.functions.linear.LinearRegression;
import io.sugo.pio.operator.learner.tree.ParallelDecisionTreeLearner;
import io.sugo.pio.operator.learner.tree.ParallelRandomForestLearner;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.operator.performance.BinominalClassificationPerformanceEvaluator;
import io.sugo.pio.operator.performance.PolynominalClassificationPerformanceEvaluator;
import io.sugo.pio.operator.performance.RegressionPerformanceEvaluator;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.NumericToBinominal;
import io.sugo.pio.operator.preprocessing.filter.NumericToPolynominal;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilter;
import io.sugo.pio.operator.preprocessing.normalization.Normalization;
import io.sugo.pio.operator.preprocessing.sampling.SamplingOperator;
import io.sugo.pio.operator.preprocessing.transformation.aggregation.AggregationOperator;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.process.ProcessManagerConfig;

import java.util.List;

/**
 */
public class ProcessPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ProcessPioModule.class.getSimpleName())
                        .registerSubtypes(
                                // dataSource
                                new NamedType(DatabaseDataReader.class, ProcessConstant.OperatorType.DatabaseDataReader),
                                new NamedType(CSVExampleSource.class, ProcessConstant.OperatorType.CSVExampleSource),
//                                new NamedType(CSVExampleSetWriter.class, ProcessConstant.OperatorType.CSVExampleSetWriter),
                                new NamedType(HttpSqlExampleSource.class, ProcessConstant.OperatorType.HttpSqlExampleSource),
                                new NamedType(SingleViewExampleSource.class, ProcessConstant.OperatorType.SingleViewExampleSource),

                                // fieldSetting
                                new NamedType(AttributeFilter.class, ProcessConstant.OperatorType.AttributeFilter),
                                new NamedType(ChangeAttributeRole.class, ProcessConstant.OperatorType.ChangeAttributeRole),
                                new NamedType(NumericToBinominal.class, ProcessConstant.OperatorType.NumericToBinominal),
                                new NamedType(NumericToPolynominal.class, ProcessConstant.OperatorType.NumericToPolynominal),
                                // filtering
                                new NamedType(ExampleFilter.class, ProcessConstant.OperatorType.ExampleFilter),
                                // sampling
                                new NamedType(SamplingOperator.class, ProcessConstant.OperatorType.SamplingOperator),
                                // normalization
                                new NamedType(Normalization.class, ProcessConstant.OperatorType.Normalization),
                                // aggregation
                                new NamedType(AggregationOperator.class, ProcessConstant.OperatorType.AggregationOperator),

                                // classification
                                new NamedType(ParallelDecisionTreeLearner.class, ProcessConstant.OperatorType.ParallelDecisionTreeLearner),
                                new NamedType(MyKLRLearner.class, ProcessConstant.OperatorType.MyKLRLearner),
                                new NamedType(JMySVMLearner.class, ProcessConstant.OperatorType.JMySVMLearner),
                                new NamedType(ParallelRandomForestLearner.class, ProcessConstant.OperatorType.ParallelRandomForestLearner),
                                // regression
                                new NamedType(LinearRegression.class, ProcessConstant.OperatorType.LinearRegression),
                                // clustering
                                new NamedType(KMeans.class, ProcessConstant.OperatorType.KMeans),
                                // association
                                new NamedType(FPGrowth.class, ProcessConstant.OperatorType.FPGrowth),
                                // modelApply
                                new NamedType(AssociationRuleGenerator.class, ProcessConstant.OperatorType.AssociationRuleGenerator),
                                new NamedType(ModelApplier.class, ProcessConstant.OperatorType.ModelApplier),
                                // modelPerformance
                                new NamedType(BinominalClassificationPerformanceEvaluator.class, ProcessConstant.OperatorType.BinominalClassificationPerformanceEvaluator),
                                new NamedType(PolynominalClassificationPerformanceEvaluator.class, ProcessConstant.OperatorType.PolynominalClassificationPerformanceEvaluator),
                                new NamedType(RegressionPerformanceEvaluator.class, ProcessConstant.OperatorType.RegressionPerformanceEvaluator)
                        )
        );
    }

    @Override
    public void configure(Binder binder) {
        JsonConfigProvider.bind(binder, "pio.process", ProcessManagerConfig.class);
        LifecycleModule.register(binder, ProcessManager.class);
    }

}
