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
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.parameter.ParameterTypeText;
import io.sugo.pio.parameter.TextType;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.util.*;

public class HttpSqlExampleSource extends AbstractExampleSource {

    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_SQL = "sql";

    private static final String[] DATETIME_FIELD_NAME = {"__time", "event_time"};

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

            DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, DataRowFactory.POINT_AS_DECIMAL_CHARACTER);
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
                        String attrName = attr.getName();
                        Object attrValue = map.get(attrName); // 当该字段为日期类型时，此值为json串，需要进一步反序列化取出真正的日期值
                        String attrValueStr = extractAttrValue(attrName, attrValue);

                        double value = attr.getMapping().mapString(attrValueStr);
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
        return I18N.getMessage("pio.HttpSqlExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.HttpSqlExampleSource.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        ParameterTypeString urlType = new ParameterTypeString(PARAMETER_URL,
                I18N.getMessage("pio.HttpSqlExampleSource.url"), false);
        types.add(urlType);

        ParameterTypeText sqlType = new ParameterTypeText(PARAMETER_SQL,
                I18N.getMessage("pio.HttpSqlExampleSource.sql"), TextType.SQL, false);
        types.add(sqlType);

        return types;
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

    private String extractAttrValue(String attrName, Object originValue) {
        if (Objects.nonNull(originValue)) {
            for (String datetimeFieldName : DATETIME_FIELD_NAME) {
                if (datetimeFieldName.equals(attrName)) {
                    ObjectReader reader = jsonMapper.readerFor(DatetimeVo.class);
                    try {
                        String valueStr = originValue == null ? null : jsonMapper.writeValueAsString(originValue);
                        DatetimeVo datetimeVo = reader.readValue(valueStr);

                        return datetimeVo.getValue();
                    } catch (IOException e) {
                        return originValue.toString();
                    }
                }
            }
        }

        return null;
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

    /**
     * 返回结果接收类
     */
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

    /**
     * 时间字段映射类。返回的时间字段不是一个字符串，而是一串json，需要做特殊处理
     * "__time": {
     * "type": "TIME",
     * "value": "2017-02-07T23:59:20.880Z"
     * }
     */
    private static class DatetimeVo {
        String type;
        String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        new HttpSqlExampleSource().createExampleSet();
    }


}
