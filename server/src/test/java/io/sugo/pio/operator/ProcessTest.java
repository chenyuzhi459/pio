package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.learner.tree.ParallelDecisionTreeLearner;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.SubsetAttributeFilter;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.*;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_NAME;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_TARGET_ROLE;
import static io.sugo.pio.operator.preprocessing.filter.ExampleFilter.PARAMETER_FILTERS_LIST;

public class ProcessTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {

        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(
//                                OperatorMapHelper.getAllOperatorMetas(jsonMapper).values()));
    }

    @Test
    public void test() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        DatabaseDataReader dbReader = new DatabaseDataReader();
        dbReader.setParameter("database_url", "jdbc:postgresql://192.168.0.210:5432/druid_perform");
        dbReader.setParameter("username", "postgres");
        dbReader.setParameter("password", "123456");
        dbReader.setParameter("query", "SELECT * from bank_sample");
        dbReader.setName("operator_db_reader");

        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        AttributeFilter af = new AttributeFilter();
        af.setName("operator_attribute_filter");
        af.setParameter(SubsetAttributeFilter.PARAMETER_ATTRIBUTES, "age;education;is_default");
        process.getRootOperator().getExecutionUnit().addOperator(af);

        ExampleFilter ef = new ExampleFilter();
        ef.setName("operator_example_filter");
        ef.setParameter(PARAMETER_FILTERS_LIST, "filters_entry_key:age.is_not_missing.;filters_entry_key:is_default.equals.Y");
        process.getRootOperator().getExecutionUnit().addOperator(ef);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME,"is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        ParallelDecisionTreeLearner dt = new ParallelDecisionTreeLearner();
        dt.setName("decision_tree");
        dt.setParameter(PARAMETER_CRITERION, "gain_ratio");
        dt.setParameter(PARAMETER_MAXIMAL_DEPTH, "20");
        dt.setParameter(PARAMETER_PRUNING, "true");
        dt.setParameter(PARAMETER_CONFIDENCE, "0.1");
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

        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(af)
        );

//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(process)
//        );
//        IOContainer set = process.run();
//        set = process.getRootOperator().getResults(true);
//        System.out.println();
//        System.out.println();
//        System.out.println(
//                jsonMapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(set)
//        );
    }
}
