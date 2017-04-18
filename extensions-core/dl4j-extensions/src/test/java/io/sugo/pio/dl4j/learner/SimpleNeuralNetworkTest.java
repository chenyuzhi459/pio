package io.sugo.pio.dl4j.learner;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.dl4j.layers.OutputLayer;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_NAME;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_TARGET_ROLE;

/**
 */
public class SimpleNeuralNetworkTest {

    private static final String DB_URL = "jdbc:postgresql://192.168.0.210:5432/druid_perform";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_QUERY = "SELECT * from bank_sample";

    @Test
    public void test() {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        String csvFilePath = getFullPath("bank_sample.csv");
        CSVExampleSource csvExampleSource = new CSVExampleSource();
        csvExampleSource.setName("operator_csv");
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, csvFilePath);
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_META_DATA,
                "0:ID.true.attribute_value.id;1:age.true.integer.attribute;2:education.true.integer.attribute;3:seniority.true.integer.attribute;4:address_year.true.integer.attribute;5:income.true.integer.attribute;6:debt_ratio.true.real.attribute;7:Credit_card_debt.true.real.attribute;8:other_debt.true.real.attribute;9:is_default.true.binominal.attribute");
        process.getRootOperator().getExecutionUnit().addOperator(csvExampleSource);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);

        SimpleNeuralNetwork simpleNeuralNetwork = new SimpleNeuralNetwork();
        simpleNeuralNetwork.setName("simpleNetwork");
        process.getRootOperator().getExecutionUnit().addOperator(simpleNeuralNetwork);

        /*DenseLayer input = new DenseLayer();
        input.setName("input");
        simpleNeuralNetwork.getExecutionUnit(0).addOperator(input);*/

        OutputLayer outputLayer = new OutputLayer();
        outputLayer.setName("output");
        simpleNeuralNetwork.getExecutionUnit(0).addOperator(outputLayer);

        outputLayer.getProcess().connect(new Connection("simpleNetwork", "start", "output", "through"), true);
        outputLayer.getProcess().connect(new Connection("output", "through", "simpleNetwork", "end"), true);

        process.connect(new Connection("operator_csv", "output", "change_role", "example set input"), true);
        process.connect(new Connection("change_role", "example set output", "simpleNetwork", "training examples"), true);
        process.run();

        IOContainer result = simpleNeuralNetwork.getResult();
        System.out.println(result);
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

    private String getFullPath(String fileName) {
        return SimpleNeuralNetworkTest.class.getResource("/" + fileName).getPath();
    }
}
