package io.sugo.pio.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import org.junit.Test;

public class DatabaseDataReaderTest {
    private static final ObjectMapper jsonMapper = new DefaultObjectMapper();

    static {
        ProcessPioModule module = new ProcessPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
    }

    @Test
    public void test() throws JsonProcessingException {

//        pio.metadata.storage.type=postgresql
//        pio.metadata.storage.connector.connectURI=jdbc:postgresql://192.168.0.210:5432/pio        druid_segments
//        pio.metadata.storage.connector.user=postgres
//        pio.metadata.storage.connector.password=123456
//        sql.setParameter("define_connection", "1");
        DatabaseDataReader dbReader = new DatabaseDataReader();
        dbReader.setParameter("database_url", "jdbc:postgresql://192.168.0.210:5432/druid_perform");
        dbReader.setParameter("username", "postgres");
        dbReader.setParameter("password", "123456");

        dbReader.setParameter("query", "SELECT * from bank_sample");

        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(dbReader)
        );

        dbReader.doWork();
        dbReader.getTransformer().transformMetaData();
    }
}
