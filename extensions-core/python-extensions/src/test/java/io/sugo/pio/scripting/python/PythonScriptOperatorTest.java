package io.sugo.pio.scripting.python;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.impl.OutputPortImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import org.junit.Ignore;
import org.junit.Test;

import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_NAME;
import static io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole.PARAMETER_TARGET_ROLE;

/**
 */
public class PythonScriptOperatorTest {
    private static final String DB_URL = "jdbc:postgresql://192.168.0.210:5432/druid_perform";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_QUERY = "SELECT * from bank_sample";

    @Test
    public void test() {
        OperatorProcess process = new OperatorProcess("testProcess");
        process.setDescription("testProcess desc");

        /*DatabaseDataReader dbReader = getDbReader();
        process.getRootOperator().getExecutionUnit().addOperator(dbReader);

        ChangeAttributeRole role = new ChangeAttributeRole();
        role.setName("change_role");
        role.setParameter(PARAMETER_NAME, "is_default");
        role.setParameter(PARAMETER_TARGET_ROLE, "label");
        process.getRootOperator().getExecutionUnit().addOperator(role);*/

        String csvFilePath = getFullPath("bank_sample.csv");
        CSVExampleSource csvExampleSource = new CSVExampleSource();
        csvExampleSource.setName("operator_csv");
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, csvFilePath);
        csvExampleSource.setParameter(CSVExampleSource.PARAMETER_META_DATA,
                "0:ID.true.attribute_value.id;1:age.true.integer.attribute;2:education.true.integer.attribute;3:seniority.true.integer.attribute;4:address_year.true.integer.attribute;5:income.true.integer.attribute;6:debt_ratio.true.real.attribute;7:Credit_card_debt.true.real.attribute;8:other_debt.true.real.attribute;9:is_default.true.binominal.attribute");
        process.getRootOperator().getExecutionUnit().addOperator(csvExampleSource);

        PythonScriptingOperator pythonScriptingOperator = new PythonScriptingOperator();
        pythonScriptingOperator.setName("python");
        pythonScriptingOperator.setParameter("script", "import pandas\n" +
                "\n" +
                "# rm_main is a mandatory function, \n" +
                "# the number of arguments has to be the number of input ports (can be none)\n" +
                "def rm_main(data):\n" +
                "    print('Hello, world!')\n" +
                "    # output can be found in Log View\n" +
                "    print(type(data))\n" +
                "\n" +
                "    #your code goes here\n" +
                "    print(data)\n" +
                "\n" +
                "    #for example:\n" +
                "    data2 = pandas.DataFrame([3,5,77,8])\n" +
                "\n" +
                "    # connect 2 output ports to see the results\n" +
                "    return data, data2");
        process.getRootOperator().getExecutionUnit().addOperator(pythonScriptingOperator);

        process.connect(new Connection("operator_csv", "output", "python", "input 1"), true);
//        process.connect(new Connection("change_role", "example set output", "python", "input 1"), true);

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

    private String getFullPath(String fileName) {
        return PythonScriptOperatorTest.class.getResource("/" + fileName).getPath();
    }
}
