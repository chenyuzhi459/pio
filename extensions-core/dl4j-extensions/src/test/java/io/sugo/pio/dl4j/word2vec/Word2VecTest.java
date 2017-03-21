package io.sugo.pio.dl4j.word2vec;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

import static io.sugo.pio.dl4j.word2vec.Word2VecLearner.PARAMETER_ATTRIBUTE_NAME;

/**
 */
public class Word2VecTest {
    private static final String DB_URL = "jdbc:postgresql://192.168.0.210:5432/mmw";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_QUERY = "SELECT * from mmq";


    @Test
    public void test() {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        Word2VecLearner word2VecLearner = new Word2VecLearner();
        word2VecLearner.setName("word2Vec");
        word2VecLearner.setParameter(PARAMETER_ATTRIBUTE_NAME, "title");
        process.getRootOperator().getExecutionUnit().addOperator(word2VecLearner);

        process.connect(new Connection("operator_db_reader", "output", "word2Vec", "example set input"), true);

        process.run();
        IOContainer result = word2VecLearner.getResult();
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
}
