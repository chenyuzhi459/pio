package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.clustering.clusterer.KMeans;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.io.HttpSqlExampleSource;
import io.sugo.pio.operator.learner.associations.AssociationRuleGenerator;
import io.sugo.pio.operator.learner.associations.fpgrowth.FPGrowth;
import io.sugo.pio.operator.learner.functions.kernel.JMySVMLearner;
import io.sugo.pio.operator.learner.functions.kernel.MyKLRLearner;
import io.sugo.pio.operator.learner.functions.linear.LinearRegression;
import io.sugo.pio.operator.learner.tree.ParallelDecisionTreeLearner;
import io.sugo.pio.operator.learner.tree.ParallelRandomForestLearner;
import io.sugo.pio.operator.performance.PolynominalClassificationPerformanceEvaluator;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.NumericToBinominal;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.SubsetAttributeFilter;
import io.sugo.pio.operator.preprocessing.normalization.Normalization;
import io.sugo.pio.operator.preprocessing.sampling.SamplingOperator;
import io.sugo.pio.operator.preprocessing.transformation.aggregation.AggregationOperator;
import io.sugo.pio.ports.Connection;
import org.junit.Ignore;
import org.junit.Test;

import static io.sugo.pio.operator.clustering.clusterer.KMeans.*;
import static io.sugo.pio.operator.clustering.clusterer.RMAbstractClusterer.PARAMETER_ADD_AS_LABEL;
import static io.sugo.pio.operator.clustering.clusterer.RMAbstractClusterer.PARAMETER_ADD_CLUSTER_ATTRIBUTE;
import static io.sugo.pio.operator.clustering.clusterer.RMAbstractClusterer.PARAMETER_REMOVE_UNLABELED;
import static io.sugo.pio.operator.learner.associations.AssociationRuleGenerator.PARAMETER_GAIN_THETA;
import static io.sugo.pio.operator.learner.associations.AssociationRuleGenerator.PARAMETER_LAPLACE_K;
import static io.sugo.pio.operator.learner.associations.AssociationRuleGenerator.PARAMETER_MIN_CONFIDENCE;
import static io.sugo.pio.operator.learner.associations.fpgrowth.FPGrowth.*;
import static io.sugo.pio.operator.learner.functions.kernel.JMySVMLearner.*;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_CONFIDENCE;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_CRITERION;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_MAXIMAL_DEPTH;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_MINIMAL_GAIN;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_MINIMAL_LEAF_SIZE;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_MINIMAL_SIZE_FOR_SPLIT;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_PRE_PRUNING;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_PRUNING;
import static io.sugo.pio.operator.learner.tree.ParallelRandomForestLearner.*;
import static io.sugo.pio.operator.performance.AbstractPerformanceEvaluator.PARAMETER_MAIN_CRITERION;
import static io.sugo.pio.operator.performance.AbstractPerformanceEvaluator.PARAMETER_SKIP_UNDEFINED_LABELS;
import static io.sugo.pio.operator.preprocessing.PreprocessingOperator.PARAMETER_CREATE_VIEW;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_NAME;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_TARGET_ROLE;
import static io.sugo.pio.operator.preprocessing.filter.ExampleFilter.PARAMETER_FILTERS_LIST;
import static io.sugo.pio.operator.preprocessing.filter.NumericToBinominal.PARAMETER_MAX;
import static io.sugo.pio.operator.preprocessing.filter.NumericToBinominal.PARAMETER_MIN;
import static io.sugo.pio.operator.preprocessing.filter.attributes.SubsetAttributeFilter.PARAMETER_ATTRIBUTES;
import static io.sugo.pio.operator.preprocessing.normalization.Normalization.PARAMETER_NORMALIZATION_METHOD;
import static io.sugo.pio.operator.preprocessing.sampling.SamplingOperator.*;
import static io.sugo.pio.operator.preprocessing.transformation.aggregation.AggregationOperator.PARAMETER_AGGREGATION_ATTRIBUTES;
import static io.sugo.pio.operator.preprocessing.transformation.aggregation.AggregationOperator.PARAMETER_GROUP_BY_ATTRIBUTES;
import static io.sugo.pio.operator.preprocessing.transformation.aggregation.AggregationOperator.PARAMETER_IGNORE_MISSINGS;
import static io.sugo.pio.tools.AttributeSubsetSelector.PARAMETER_FILTER_TYPE;
import static io.sugo.pio.tools.math.similarity.DistanceMeasures.PARAMETER_MEASURE_TYPES;
import static io.sugo.pio.tools.math.similarity.DistanceMeasures.PARAMETER_MIXED_MEASURE;

public class ProcessTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    private static final String DB_URL = "jdbc:postgresql://192.168.0.210:5432/druid_perform";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_QUERY = "SELECT * from bank_sample";

    static {

        ProcessPioModule module = new ProcessPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(
//                                OperatorMapHelper.getAllOperatorMetas(jsonMapper).values()));
    }

    @Test
    public void decisionTreeTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        ParallelDecisionTreeLearner dt = new ParallelDecisionTreeLearner();
        dt.setName("decision_tree");
        dt.setParameter(PARAMETER_CRITERION, "gain_ratio");
        dt.setParameter(PARAMETER_MAXIMAL_DEPTH, "20");
        dt.setParameter(PARAMETER_PRUNING, "true");
        dt.setParameter(PARAMETER_CONFIDENCE, "0.25");
        dt.setParameter(PARAMETER_PRE_PRUNING, "true");
        dt.setParameter(PARAMETER_MINIMAL_GAIN, "0.1");
        dt.setParameter(PARAMETER_MINIMAL_LEAF_SIZE, "2");
        dt.setParameter(PARAMETER_MINIMAL_SIZE_FOR_SPLIT, "4");
        dt.setParameter(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES, "3");
        process.getRootOperator().getExecutionUnit().addOperator(dt);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "decision_tree", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(af)
//        );

//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(process)
//        );
        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
//
//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(role.getResult())
//        );
        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(dt.getResult())
        );
        System.out.println();
//        System.out.println();
//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(set)
//        );
    }

    @Test
    public void linearRegressionTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("linear_regression");
        process.setDescription("linear_regression test.");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter("attribute_name", "is_default");
        role.setParameter("target_role", "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setParameter("feature_selection", "M5 prime");
        linearRegression.setParameter("eliminate_colinear_features", "true");
        linearRegression.setParameter("min_tolerance", "0.05");
        linearRegression.setParameter("use_bias", "true");
        linearRegression.setParameter("ridge", "1.0E-8");
        linearRegression.setName("linear_regression");
        process.getRootOperator().getExecutionUnit().addOperator(linearRegression);

        process.connect(new Connection("operator_db_reader", "output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "linear_regression", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(linearRegression.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void logisticRegressionTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("logistic_regression process");
        process.setDescription("logistic_regression test.");

        DatabaseDataReader dbReader = new DatabaseDataReader();
        dbReader.setParameter("database_url", DB_URL);
        dbReader.setParameter("username", DB_USERNAME);
        dbReader.setParameter("password", DB_PASSWORD);
        dbReader.setParameter("query", "SELECT * from lr_sample");
        dbReader.setName("operator_db_reader");
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "class;attribute_1;attribute_2;attribute_3;attribute_4;attribute_5;attribute_6;attribute_7;attribute_8;attribute_9;attribute_10");
        process.getRootOperator().getExecutionUnit().addOperator(af);

       ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter("attribute_name", "class");
        role.setParameter("target_role", "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        MyKLRLearner logisticRegression = new MyKLRLearner();
        logisticRegression.setParameter(PARAMETER_KERNEL_TYPE, "0");
        logisticRegression.setParameter(PARAMETER_KERNEL_CACHE, "200");
        logisticRegression.setParameter(PARAMETER_C, "1.0");
        logisticRegression.setParameter(PARAMETER_CONVERGENCE_EPSILON, "0.001");
        logisticRegression.setParameter(PARAMETER_MAX_ITERATIONS, "100000");
        logisticRegression.setParameter(PARAMETER_SCALE, "true");
        logisticRegression.setName("logistic_regression");
        process.getRootOperator().getExecutionUnit().addOperator(logisticRegression);

        process.connect(new Connection("operator_db_reader", "output", "attribute_filter", "example set input"), true);
        process.connect(new Connection("attribute_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "logistic_regression", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logisticRegression.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void svmTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("svm_test");
        process.setDescription("svm_test");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        JMySVMLearner svm = new JMySVMLearner();
        svm.setName("svm");
        svm.setParameter(PARAMETER_KERNEL_TYPE, "0");
        svm.setParameter(PARAMETER_KERNEL_CACHE, "200");
        svm.setParameter(PARAMETER_C, "0.0");
        svm.setParameter(PARAMETER_CONVERGENCE_EPSILON, "0.001");
        svm.setParameter(PARAMETER_MAX_ITERATIONS, "100000");
        svm.setParameter(PARAMETER_SCALE, "false");
        svm.setParameter(PARAMETER_L_POS, "1.0");
        svm.setParameter(PARAMETER_L_NEG, "1.0");
        svm.setParameter(PARAMETER_EPSILON, "0.0");
        svm.setParameter(PARAMETER_EPSILON_PLUS, "0.0");
        svm.setParameter(PARAMETER_EPSILON_MINUS, "0.0");
        svm.setParameter(PARAMETER_BALANCE_COST, "false");
        svm.setParameter(PARAMETER_QUADRATIC_LOSS_POS, "false");
        svm.setParameter(PARAMETER_QUADRATIC_LOSS_NEG, "false");
        process.getRootOperator().getExecutionUnit().addOperator(svm);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "svm", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
//        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(svm.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void randomForestTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("random_forest_test");
        process.setDescription("random_forest_test");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        ParallelRandomForestLearner randomForest = new ParallelRandomForestLearner();
        randomForest.setName("random_forest");
        randomForest.setParameter(PARAMETER_USE_HEURISTIC_SUBSET_RATION, "true");
        randomForest.setParameter(PARAMETER_NUMBER_OF_TREES, "10");
        randomForest.setParameter(PARAMETER_VOTING_STRATEGY, "0");
        randomForest.setParameter(PARAMETER_CRITERION, "gain_ratio");
        randomForest.setParameter(PARAMETER_MAXIMAL_DEPTH, "20");
        randomForest.setParameter(PARAMETER_PRUNING, "true");
        randomForest.setParameter(PARAMETER_CONFIDENCE, "0.25");
        randomForest.setParameter(PARAMETER_MINIMAL_GAIN, "0.1");
        randomForest.setParameter(PARAMETER_MINIMAL_LEAF_SIZE, "2");
        randomForest.setParameter(PARAMETER_MINIMAL_SIZE_FOR_SPLIT, "4");
        randomForest.setParameter(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES, "3");
        process.getRootOperator().getExecutionUnit().addOperator(randomForest);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "random_forest", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
//        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(randomForest.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void kmeansTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("k_means_test");
        process.setDescription("k_means_test");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        KMeans kMeans = new KMeans();
        kMeans.setName("k_means");
        kMeans.setParameter(PARAMETER_K, "2");
        kMeans.setParameter(PARAMETER_MAX_RUNS, "10");
        kMeans.setParameter(PARAMETER_MAX_OPTIMIZATION_STEPS, "100");
        kMeans.setParameter(PARAMETER_ADD_CLUSTER_ATTRIBUTE, "true");
        kMeans.setParameter(PARAMETER_REMOVE_UNLABELED, "false");
        kMeans.setParameter(PARAMETER_ADD_AS_LABEL, "false");
        kMeans.setParameter(PARAMETER_MEASURE_TYPES, "MixedMeasures");
        kMeans.setParameter(PARAMETER_MIXED_MEASURE, "MixedEuclideanDistance");
        process.getRootOperator().getExecutionUnit().addOperator(kMeans);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "k_means", "example set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kMeans.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void sampleTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("k_means_test");
        process.setDescription("k_means_test");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        SamplingOperator sample = new SamplingOperator();
        sample.setName("sample");
//        sample.setParameter(PARAMETER_SAMPLE, "absolute"); // {"absolute", "relative", "probability"}
//        sample.setParameter(PARAMETER_SAMPLE_SIZE, "100");

//        sample.setParameter(PARAMETER_SAMPLE, "relative");
//        sample.setParameter(PARAMETER_SAMPLE_RATIO, "0.1");
//
        sample.setParameter(PARAMETER_SAMPLE, "probability");
        sample.setParameter(PARAMETER_SAMPLE_PROBABILITY, "0.2");
        process.getRootOperator().getExecutionUnit().addOperator(sample);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "sample", "example set input"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sample.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
//    @Ignore
    public void performanceClassificationTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("performance classification test");
        process.setDescription("performance classification test desc");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:is_default.is_not_missing.");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        ParallelDecisionTreeLearner dt = new ParallelDecisionTreeLearner();
        dt.setName("decision_tree");
        dt.setParameter(PARAMETER_CRITERION, "gain_ratio");
        dt.setParameter(PARAMETER_MAXIMAL_DEPTH, "20");
        dt.setParameter(PARAMETER_PRUNING, "true");
        dt.setParameter(PARAMETER_CONFIDENCE, "0.25");
        dt.setParameter(PARAMETER_PRE_PRUNING, "true");
        dt.setParameter(PARAMETER_MINIMAL_GAIN, "0.1");
        dt.setParameter(PARAMETER_MINIMAL_LEAF_SIZE, "2");
        dt.setParameter(PARAMETER_MINIMAL_SIZE_FOR_SPLIT, "4");
        dt.setParameter(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES, "3");
        process.getRootOperator().getExecutionUnit().addOperator(dt);

        ModelApplier modelApplier = new ModelApplier();
        modelApplier.setName("apply_model");
        process.getRootOperator().getExecutionUnit().addOperator(modelApplier);

        PolynominalClassificationPerformanceEvaluator pcp = new PolynominalClassificationPerformanceEvaluator();
        pcp.setName("performance_classification");
        pcp.setParameter(PARAMETER_MAIN_CRITERION, "first");
        pcp.setParameter("accuracy", "true");
        pcp.setParameter(PARAMETER_SKIP_UNDEFINED_LABELS, "true");
        process.getRootOperator().getExecutionUnit().addOperator(pcp);

        /********* test operator *********/
        DatabaseDataReader dbReader4test = new DatabaseDataReader();
        dbReader4test.setParameter("database_url", DB_URL);
        dbReader4test.setParameter("username", DB_USERNAME);
        dbReader4test.setParameter("password", DB_PASSWORD);
        dbReader4test.setParameter("query", "select * from bank_sample");
        dbReader4test.setName("operator_db_reader_test");
        process.getRootOperator().getExecutionUnit().addOperator(dbReader4test);

        SamplingOperator sample4test = new SamplingOperator();
        sample4test.setName("sample_test");
        sample4test.setParameter(PARAMETER_SAMPLE, "absolute");
        sample4test.setParameter(PARAMETER_SAMPLE_SIZE, "100");
        process.getRootOperator().getExecutionUnit().addOperator(sample4test);

        ChangeAttributeRole role4test = new ChangeAttributeRole();
        role4test.setName("change_role_test");
        role4test.setParameter(PARAMETER_NAME, "is_default");
        role4test.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role4test);

        /********* test operator end *********/

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "operator_example_filter", "example set input"), true);
        process.connect(new Connection("operator_example_filter", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "decision_tree", "training set"), true);
        process.connect(new Connection("decision_tree", "model", "apply_model", "model"), true);
        process.connect(new Connection("apply_model", "labelled data", "performance_classification", "labelled data"), true);

        process.connect(new Connection("operator_db_reader_test", "output", "sample_test", "example set input"), true);
        process.connect(new Connection("sample_test", "example set output", "change_role_test", "example set input"), true);
        process.connect(new Connection("change_role_test", "example set output", "apply_model", "unlabelled data"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pcp.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void normalizationTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("normalization_test");
        process.setDescription("normalization_test");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "age;address_year;income");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        Normalization normalization = new Normalization();
        normalization.setName("normalization");
        normalization.setParameter(PARAMETER_NORMALIZATION_METHOD, "0");
//        normalization.setParameter("min", "1");
//        normalization.setParameter("max", "10");
        normalization.setParameter(PARAMETER_CREATE_VIEW, "false");
        normalization.setParameter(PARAMETER_FILTER_TYPE, "0");
        normalization.setParameter(PARAMETER_ATTRIBUTES, "age;address_year;income");
        process.getRootOperator().getExecutionUnit().addOperator(normalization);

        SamplingOperator sample = new SamplingOperator();
        sample.setName("sample");
        sample.setParameter(PARAMETER_SAMPLE, "absolute");
        sample.setParameter(PARAMETER_SAMPLE_SIZE, "100");
        process.getRootOperator().getExecutionUnit().addOperator(sample);

        process.connect(new Connection("operator_db_reader", "output", "operator_attribute_filter", "example set input"), true);
        process.connect(new Connection("operator_attribute_filter", "example set output", "normalization", "example set input"), true);
        process.connect(new Connection("normalization", "example set output", "sample", "example set input"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(normalization.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void fpgrowthTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("fp_growth_test");
        process.setDescription("fp_growth_test");

        DatabaseDataReader dbReader = new DatabaseDataReader();
        dbReader.setParameter("database_url", DB_URL);
        dbReader.setParameter("username", DB_USERNAME);
        dbReader.setParameter("password", DB_PASSWORD);
        dbReader.setParameter("query", "SELECT * from shopping_basket");
        dbReader.setName("operator_db_reader");
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("attribute_filter");
        af.setParameter(PARAMETER_ATTRIBUTES, "会员ID;商品_冻肉;商品_啤酒;商品_牛奶;商品_甜食;商品_罐装肉;商品_罐装蔬菜;商品_葡萄酒;商品_蔬菜水果;商品_饮料;商品_鱼;商品_鲜肉");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        NumericToBinominal numeric2binominal = new NumericToBinominal();
        numeric2binominal.setName("numeric2binominal");
        numeric2binominal.setParameter(PARAMETER_FILTER_TYPE, "2");
        numeric2binominal.setParameter(PARAMETER_ATTRIBUTES, "商品_冻肉;商品_啤酒;商品_牛奶;商品_甜食;商品_罐装肉;商品_罐装蔬菜;商品_葡萄酒;商品_蔬菜水果;商品_饮料;商品_鱼;商品_鲜肉");
        numeric2binominal.setParameter(PARAMETER_MIN, "0.0");
        numeric2binominal.setParameter(PARAMETER_MAX, "0.0");
        process.getRootOperator().getExecutionUnit().addOperator(numeric2binominal);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "会员ID");
        role.setParameter(PARAMETER_TARGET_ROLE, "id");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        FPGrowth fpGrowth = new FPGrowth();
        fpGrowth.setName("fp_growth");
//        fpGrowth.setParameter(PARAMETER_MIN_NUMBER_OF_ITEMSETS, "100");
//        fpGrowth.setParameter(PARAMETER_MAX_REDUCTION_STEPS, "10");
        fpGrowth.setParameter(PARAMETER_POSITIVE_VALUE, "true");
        fpGrowth.setParameter(PARAMETER_MIN_SUPPORT, "0.4");
        fpGrowth.setParameter(PARAMETER_MAX_ITEMS, "-1");
        fpGrowth.setParameter(PARAMETER_MUST_CONTAIN, "");
        process.getRootOperator().getExecutionUnit().addOperator(fpGrowth);

        AssociationRuleGenerator associationRuleGenerator = new AssociationRuleGenerator();
        associationRuleGenerator.setName("association_rule_generator");
        associationRuleGenerator.setParameter(PARAMETER_CRITERION, "0");
        associationRuleGenerator.setParameter(PARAMETER_MIN_CONFIDENCE, "0.4");
        associationRuleGenerator.setParameter(PARAMETER_GAIN_THETA, "2.0");
        associationRuleGenerator.setParameter(PARAMETER_LAPLACE_K, "1.0");
        process.getRootOperator().getExecutionUnit().addOperator(associationRuleGenerator);

        process.connect(new Connection("operator_db_reader", "output", "attribute_filter", "example set input"), true);
        process.connect(new Connection("attribute_filter", "example set output", "numeric2binominal", "example set input"), true);
        process.connect(new Connection("numeric2binominal", "example set output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "fp_growth", "example set"), true);
        process.connect(new Connection("fp_growth", "frequent sets", "association_rule_generator", "item sets"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(associationRuleGenerator.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    @Test
    public void aggregateRegressionTest() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("aggregate");
        process.setDescription("aggregate test.");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AggregationOperator aggregate = new AggregationOperator();
        aggregate.setName("aggregate");
//        aggregate.setParameter(PARAMETER_AGGREGATION_ATTRIBUTES, "income:average");
        aggregate.setParameter(PARAMETER_AGGREGATION_ATTRIBUTES, "income:count");
//        aggregate.setParameter(PARAMETER_AGGREGATION_ATTRIBUTES, "income:least");
        aggregate.setParameter(PARAMETER_GROUP_BY_ATTRIBUTES, "education");
        aggregate.setParameter(PARAMETER_IGNORE_MISSINGS, "true");
        process.getRootOperator().getExecutionUnit().addOperator(aggregate);

        process.connect(new Connection("operator_db_reader", "output", "aggregate", "example set input"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aggregate.getResult()));
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
    }

    private DatabaseDataReader getDbReader() {
        DatabaseDataReader dbReader = new DatabaseDataReader();
        dbReader.setParameter("database_url", DB_URL);
        dbReader.setParameter("username", DB_USERNAME);
        dbReader.setParameter("password", DB_PASSWORD);
        dbReader.setParameter("query", DB_QUERY);
        dbReader.setName("operator_db_reader");

        return dbReader;
    }
}
