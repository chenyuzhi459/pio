package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.operator.ModelApplier;
import io.sugo.pio.operator.clustering.clusterer.KMeans;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
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
                                new NamedType(DatabaseDataReader.class, "db_data_reader"),
                                new NamedType(AttributeFilter.class, "select_attributes"),
                                new NamedType(ExampleFilter.class, "filter_examples"),
                                new NamedType(ChangeAttributeRole.class, "set_role"),
                                new NamedType(Normalization.class, "normalization"),
                                new NamedType(AggregationOperator.class, "aggregate"),

                                new NamedType(NumericToBinominal.class, "numeric2binominal"),
                                new NamedType(NumericToPolynominal.class, "numeric2polynominal"),

                                new NamedType(ParallelDecisionTreeLearner.class, "parallel_decision_tree"),
                                new NamedType(ParallelRandomForestLearner.class, "random_forest"),
//                                new NamedType(LogisticRegression.class, "logistic_regression"),
                                new NamedType(MyKLRLearner.class, "logistic_regression"),
                                new NamedType(LinearRegression.class, "linear_regression"),
                                new NamedType(KMeans.class, "k_means"),
                                new NamedType(JMySVMLearner.class, "support_vector_machine"),
                                new NamedType(FPGrowth.class, "fp_growth"),
                                new NamedType(AssociationRuleGenerator.class, "create_association_rules"),

                                new NamedType(PolynominalClassificationPerformanceEvaluator.class, "performance_classification"),
                                new NamedType(BinominalClassificationPerformanceEvaluator.class, "performance_binominal_classification"),
                                new NamedType(RegressionPerformanceEvaluator.class, "performance_regression"),

                                new NamedType(SamplingOperator.class, "sample"),
                                new NamedType(ModelApplier.class, "apply_model"),

                                new NamedType(CSVExampleSource.class, "read_csv"),
                                new NamedType(HttpSqlExampleSource.class, "http_sql_source"),
                                new NamedType(SingleViewExampleSource.class, "single_view_source")
                        )
        );
    }

    @Override
    public void configure(Binder binder) {
        JsonConfigProvider.bind(binder, "pio.process", ProcessManagerConfig.class);
        LifecycleModule.register(binder, ProcessManager.class);
    }

}
