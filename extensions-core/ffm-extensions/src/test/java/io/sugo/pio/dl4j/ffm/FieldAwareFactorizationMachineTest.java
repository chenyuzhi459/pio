package io.sugo.pio.dl4j.ffm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.ffm.FieldAwareFactorizationMachine;
import io.sugo.pio.operator.ModelApplier;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.ports.Connection;
import org.junit.Test;

/**
 */
public class FieldAwareFactorizationMachineTest {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String DB_URL = "jdbc:postgresql://192.168.0.210:5432/druid_perform";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_QUERY = "SELECT * from bank_sample";

    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
        jsonMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
        jsonMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        jsonMapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
        jsonMapper.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, false);
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    @Test
    public void test() throws JsonProcessingException {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        /*DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);*/
        String csvFilePath = getFullPath("bank_sample.csv");

        CSVExampleSource csvExampleSource = new CSVExampleSource();
        csvExampleSource.setName("operator_csv");
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, csvFilePath);
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_META_DATA,
                "0:ID.true.attribute_value.id;1:age.true.integer.attribute;2:education.true.integer.attribute;3:seniority.true.integer.attribute;4:address_year.true.integer.attribute;5:income.true.integer.attribute;6:debt_ratio.true.real.attribute;7:Credit_card_debt.true.real.attribute;8:other_debt.true.real.attribute;9:is_default.true.binominal.label");
//        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_COLUMN_SEPARATORS, "\\s");
        process.getRootOperator().getExecutionUnit().addOperator(csvExampleSource);

        FieldAwareFactorizationMachine ffm = new FieldAwareFactorizationMachine();
        ffm.setName("ffm");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_ITERATION, "15");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_LEARNING_RATE, "0.1f");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_L2, "0");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_NORMALIZATION, "true");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_RANDOM, "true");
        ffm.setParameter(FieldAwareFactorizationMachine.PARAMETER_LATENT_FACTOR_DIM, "4");
        process.getRootOperator().getExecutionUnit().addOperator(ffm);

        ModelApplier modelApplier = new ModelApplier();
        modelApplier.setName("apply_model");
        process.getRootOperator().getExecutionUnit().addOperator(modelApplier);

        CSVExampleSource csvExampleSource4test = new CSVExampleSource();
        csvExampleSource4test.setName("operator_csv_test");
        csvExampleSource4test.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, csvFilePath);
        csvExampleSource4test.setParameter(CSVExampleSource.PARAMETER_META_DATA,
                "0:ID.true.attribute_value.id;1:age.true.integer.attribute;2:education.true.integer.attribute;3:seniority.true.integer.attribute;4:address_year.true.integer.attribute;5:income.true.integer.attribute;6:debt_ratio.true.real.attribute;7:Credit_card_debt.true.real.attribute;8:other_debt.true.real.attribute;9:is_default.true.binominal.label");
        process.getRootOperator().getExecutionUnit().addOperator(csvExampleSource4test);

        process.connect(new Connection("operator_csv", "output", "ffm", "training examples"), true);
        process.connect(new Connection("ffm", "model", "apply_model", "model"), true);
//        process.connect(new Connection("apply_model", "labelled data", "performance_classification", "labelled data"), true);

        process.connect(new Connection("operator_csv_test", "output", "apply_model", "unlabelled data"), true);

        process.run();

        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString((ffm.getResult().getIoObjects()))
        );
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
        return FieldAwareFactorizationMachineTest.class.getResource("/" + fileName).getPath();
    }
}
