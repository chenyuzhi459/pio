package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.io.HttpSqlExampleSource;
import io.sugo.pio.operator.learner.functions.kernel.JMySVMLearner;
import io.sugo.pio.operator.learner.functions.linear.LinearRegression;
import io.sugo.pio.operator.learner.tree.ParallelDecisionTreeLearner;
import io.sugo.pio.operator.learner.tree.ParallelRandomForestLearner;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.SubsetAttributeFilter;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

import static io.sugo.pio.operator.learner.functions.kernel.JMySVMLearner.*;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.*;
import static io.sugo.pio.operator.learner.tree.ParallelRandomForestLearner.*;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_NAME;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_TARGET_ROLE;
import static io.sugo.pio.operator.preprocessing.filter.ExampleFilter.PARAMETER_FILTERS_LIST;

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
        af.setParameter(SubsetAttributeFilter.PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
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
        OperatorProcess process = new OperatorProcess("httpSql");
        process.setDescription("Huangama http sql test.");

        HttpSqlExampleSource httpSqlSource = new HttpSqlExampleSource();
        httpSqlSource.setParameter("url", "http://192.168.0.212:8000/api/plyql/sql");
        httpSqlSource.setParameter("sql", "select * from wuxianjiRT limit 10");
        httpSqlSource.setName("http_sql");
        process.getRootOperator().getExecutionUnit().addOperator(httpSqlSource);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter("attribute_name", "Nation");
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

        process.connect(new Connection("http_sql", "output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "linear_regression", "training set"), true);

        process.getRootOperator().getExecutionUnit().transformMetaData();

        IOContainer set = process.run();
        set = process.getRootOperator().getResults(true);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(linearRegression.getResult()));
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
        af.setParameter(SubsetAttributeFilter.PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
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
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
        af.setParameter(SubsetAttributeFilter.PARAMETER_ATTRIBUTES, "age;seniority;education;address_year;income;debt_ratio;Credit_card_debt;other_debt;is_default");
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
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(randomForest.getResult()));
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
