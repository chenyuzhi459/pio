package io.sugo.pio.operator.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.util.ExampleSetBuilder;
import io.sugo.pio.example.util.ExampleSets;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.util.*;

public class SqlExampleSource extends AbstractExampleSource {

    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_SQL = "sql";

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
//        String postUrl = getParameterAsString(PARAMETER_URL);
        String postUrl = "http://192.168.0.212:8000/api/plyql/sql";
        String queryParam = buildQueryParam();

        String result = null;
        try {
            result = HttpClientUtil.post(postUrl, queryParam);
        } catch (IOException e) {
            throw new OperatorException("Http post failed: " + e, e);
        }

        if (result != null) {
            List<HashMap<String, Object>> resultList = null;
            try {
                resultList = parseResult(result);
            } catch (IOException e) {
                throw new OperatorException("Parse result failed: " + e, e);
            }

            DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.');
            List<Attribute> attributes = getAttributes(resultList);
            ExampleSetBuilder builder = ExampleSets.from(attributes);

            if (resultList != null && !resultList.isEmpty()) {
                int attrSize = attributes.size();

                // traverse all rows to store data
                for (HashMap<String, Object> map : resultList) {
                    // one result corresponds to one data row
                    DataRow dataRow = factory.create(attrSize);
                    for (int i = 0; i < attrSize; i++) {
                        Attribute attr = attributes.get(i);
                        // TODO：特殊key的处理（__time, event_time）
                        String attrName = attr.getName();
                        Object attrValue = map.get(attrName);
                        double value = attr.getMapping().mapString(attrValue == null ? null : attrValue.toString());

                        dataRow.set(attr, value);
                    }

                    builder.addDataRow(dataRow);
                }
            }

            return builder.build();
        }

        return null;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.SqlExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.SqlExampleSource.description");
    }

    private List<Attribute> getAttributes(List<HashMap<String, Object>> resultList) {
        List<Attribute> attributes = new ArrayList<>();

        if (resultList == null || resultList.isEmpty()) {
            return attributes;
        }

        // create table based on the first row
        HashMap<String, Object> firstRow = resultList.get(0);
        Iterator<String> keyIterator = firstRow.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            attributes.add(AttributeFactory.createAttribute(key, Ontology.STRING));
        }

        return attributes;
    }

    private String buildQueryParam() {
//        String sql = getParameterAsString(PARAMETER_SQL);
//        String sql = "select ClientDeviceAgent,ClientDeviceBrand,Province,SessionID,__time from wuxianjiRT limit 10";
        String sql = "select * from wuxianjiRT limit 10";
        QueryVo queryVo = new QueryVo();
        queryVo.setQuery(sql);

        String queryParam = null;
        ObjectWriter writer = jsonMapper.writerFor(QueryVo.class);

        try {
            queryParam = writer.writeValueAsString(queryVo);
        } catch (JsonProcessingException ignore) {
        }

        return queryParam;
    }

    private List<HashMap<String, Object>> parseResult(String result) throws IOException {
        ObjectReader reader = jsonMapper.readerFor(ResultVo.class);
        ResultVo resultVo = reader.readValue(result);

        return resultVo.getResult();
    }

    private static class QueryVo {
        String query; // query key

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    private static class ResultVo {
        List<HashMap<String, Object>> result;
        Integer code;

        public List<HashMap<String, Object>> getResult() {
            return result;
        }

        public void setResult(List<HashMap<String, Object>> result) {
            this.result = result;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    public static void main(String[] args) {
        new SqlExampleSource().createExampleSet();
    }


}
