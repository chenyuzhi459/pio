package io.sugo.pio.constant;

/**
 */
public class ProcessConstant {

    public interface Type {
        public static final String DRAIN_TRAINING = "drain_training";
        public static final String DRAIN_PREDICTION = "drain_prediction";
    }

    public interface BuiltIn {
        public static final int NO = 0;
        public static final int YES = 1;
    }

    public interface IsTemplate {
        public static final int NO = 0;
        public static final int YES = 1;
    }

    public interface IsCase {
        public static final int NO = 0;
        public static final int YES = 1;
    }

    public interface OperatorType {
        public static final String DatabaseDataReader = "db_data_reader";
        public static final String AttributeFilter = "select_attributes";
        public static final String ExampleFilter = "filter_examples";
        public static final String ChangeAttributeRole = "set_role";
        public static final String Normalization = "normalization";
        public static final String AggregationOperator = "aggregate";
        public static final String NumericToBinominal = "numeric2binominal";
        public static final String NumericToPolynominal = "numeric2polynominal";
        public static final String ParallelDecisionTreeLearner = "parallel_decision_tree";
        public static final String ParallelRandomForestLearner = "random_forest";
        public static final String MyKLRLearner = "logistic_regression";
        public static final String LinearRegression = "linear_regression";
        public static final String KMeans = "k_means";
        public static final String JMySVMLearner = "support_vector_machine";
        public static final String FPGrowth = "fp_growth";
        public static final String AssociationRuleGenerator = "create_association_rules";
        public static final String PolynominalClassificationPerformanceEvaluator = "performance_classification";
        public static final String BinominalClassificationPerformanceEvaluator = "performance_binominal_classification";
        public static final String RegressionPerformanceEvaluator = "performance_regression";
        public static final String SamplingOperator = "sample";
        public static final String ModelApplier = "apply_model";
        public static final String CSVExampleSource = "read_csv";
        public static final String HttpSqlExampleSource = "http_sql_source";
        public static final String SingleViewExampleSource = "single_view_source";
    }
}
