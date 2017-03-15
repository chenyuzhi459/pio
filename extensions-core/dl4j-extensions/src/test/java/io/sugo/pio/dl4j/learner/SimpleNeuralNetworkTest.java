package io.sugo.pio.dl4j.learner;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.dl4j.layers.DenseLayer;
import io.sugo.pio.dl4j.layers.OutputLayer;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

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

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        SimpleNeuralNetwork simpleNeuralNetwork = new SimpleNeuralNetwork();
        simpleNeuralNetwork.setName("simpleNetwork");
        process.getRootOperator().getExecutionUnit().addOperator(simpleNeuralNetwork);
        DenseLayer input = new DenseLayer();
        input.setName("input");
        simpleNeuralNetwork.getExecutionUnit().addOperator(input);

        process.connect(new Connection("operator_db_reader", "output", "simpleNetwork", "training examples"), true);
        process.run();
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
