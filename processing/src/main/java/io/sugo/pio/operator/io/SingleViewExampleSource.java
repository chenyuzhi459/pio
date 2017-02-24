package io.sugo.pio.operator.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
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
import io.sugo.pio.parameter.*;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class SingleViewExampleSource extends AbstractExampleSource {

    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_DATA_SOURCE = "data_source";

    public static final String PARAMETER_SINGLE_VIEW_DATA_SOURCE = "single_view_data_source";

    private static final String URI_LIST_DATA_SOURCE = "/datasources/list";

    private static final String URI_LIST_SINGLE_MAP = "/slices/list/";

    private static final String URI_QUERY_DRUID = "/slices/query-druid";

    private static final Pattern urlPattern = Pattern.compile("(http|https){1}://");

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
//        String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
        String listDataSourceUrl = "http://192.168.0.32:8080/api/datasources/list";
        String druidUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_QUERY_DRUID);
        String selectedSingleView = getParameterAsString(PARAMETER_SINGLE_VIEW_DATA_SOURCE);

        ParameterTypeDynamicCategory dynamicCategory = (ParameterTypeDynamicCategory)(getParameters().getParameterType(selectedSingleView));
//        String singleViewValue = dynamicCategory.getCategoryValue(selectedSingleView);
        String singleViewValue = "{\n" +
                "\"druid_datasource_id\": \"rydmYyUYzl\",\n" +
                "      \"params\": {\n" +
                "        \"filters\": [\n" +
                "          {\n" +
                "            \"eq\": \"-3 days\",\n" +
                "            \"op\": \"in\",\n" +
                "            \"col\": \"__time\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"metrics\": [\n" +
                "          \"wuxianjiRT_total\"\n" +
                "        ],\n" +
                "        \"vizType\": \"table\",\n" +
                "        \"timezone\": \"Asia/Shanghai\",\n" +
                "        \"dimensions\": [\n" +
                "          \"Province\"\n" +
                "        ],\n" +
                "        \"dimensionExtraSettingDict\": {\n" +
                "          \"Province\": {\n" +
                "            \"limit\": 10,\n" +
                "            \"sortCol\": \"wuxianjiRT_total\",\n" +
                "            \"sortDirect\": \"desc\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "      }";

        String result = null;
        try {
            result = HttpClientUtil.post(druidUrl, singleViewValue);
        } catch (IOException e) {
            throw new OperatorException("Http post failed: " + e, e);
        }

        if (result != null) {
            List<Object> resultList = null;
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
                for (Object resultObj : resultList) {
                    String resultStr;
                    Map resultMap = null;
                    try {
                        resultStr = jsonMapper.writeValueAsString(resultObj);
                        if (Objects.nonNull(resultStr)) {
                            resultMap = jsonMapper.readValue(resultStr, Map.class);
                        }
                    } catch (IOException e) {
                        throw new OperatorException("Parse result failed: " + e, e);
                    }

                    // one result corresponds to one data row
                    DataRow dataRow = factory.create(attrSize);
                    for (int i = 0; i < attrSize; i++) {
                        Attribute attr = attributes.get(i);
                        String attrName = attr.getName();
                        Object attrValue = resultMap.get(attrName);
                        String attrValueStr = attrValue == null ? null : attrValue.toString();

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
        return I18N.getMessage("pio.SingleViewExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.SingleViewExampleSource.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        ParameterTypeString urlType = new ParameterTypeString(PARAMETER_URL,
                I18N.getMessage("pio.SingleViewExampleSource.url"), false);
        types.add(urlType);

        ParameterTypeDynamicCategory dataSourceType = getDataSourceParamType();
        types.add(dataSourceType);

        ParameterTypeDynamicCategory singleViewDataSourceType = getSingleViewParamType();
        types.add(singleViewDataSourceType);

        return types;
    }

    private boolean isValidUrl(String url) {
//        return !Strings.isNullOrEmpty(url) && urlPattern.matcher(url).matches();
        return !Strings.isNullOrEmpty(url);
    }

    private ParameterTypeDynamicCategory getSingleViewParamType() {
        String dataSourceName = getParameterAsString(PARAMETER_DATA_SOURCE);
        if (!Strings.isNullOrEmpty(dataSourceName)) {
            List<SingleMapVo> singleMapList = listSingleView();
            if (singleMapList != null && !singleMapList.isEmpty()) {
                String[] categories = new String[singleMapList.size()];
                String[] categoryValues = new String[singleMapList.size()];

                for (int i=0; i < singleMapList.size(); i++) {
                    SingleMapVo singleMap = singleMapList.get(i);
                    categories[i] = singleMap.getDatasource_name();

                    SingleMapRequestVo requestVo = new SingleMapRequestVo();
                    requestVo.setDruid_datasource_id(singleMap.getDruid_datasource_id());
                    requestVo.setParams(singleMap.getParams());
                    try {
                        String requestStr = jsonMapper.writeValueAsString(requestVo);
                        categoryValues[i] = requestStr;
                    } catch (JsonProcessingException ignore) { }
                }

                return new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW_DATA_SOURCE, PARAMETER_DATA_SOURCE,
                                I18N.getMessage("pio.SingleViewExampleSource.data_source"),
                                categories, categoryValues, categories[0]);
            }
        }

        return new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW_DATA_SOURCE,
                PARAMETER_DATA_SOURCE,
                I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"));
    }

    private ParameterTypeDynamicCategory getDataSourceParamType() {
        String url = getParameterAsString(PARAMETER_URL);
        if (isValidUrl(url)) {
            List<DataSourceVo> dataSourceList = listDataSource();
            if (dataSourceList != null && !dataSourceList.isEmpty()) {
                String[] categories = new String[dataSourceList.size()];
                String[] categoryValues = new String[dataSourceList.size()];

                for (int i=0; i < dataSourceList.size(); i++) {
                    DataSourceVo dataSource = dataSourceList.get(i);
                    categories[i] = dataSource.getTitle();
                    categoryValues[i] = dataSource.getName();
                }

                return new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, PARAMETER_URL,
                        I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"),
                        categories, categoryValues, categories[0]);

            }
        }

        return new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, PARAMETER_URL,
                I18N.getMessage("pio.SingleViewExampleSource.data_source"));
    }

    private List<DataSourceVo> listDataSource() {
        String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
//        String listDataSourceUrl = "http://192.168.0.32:8080/api/datasources/list";

        String result;
        try {
            result = HttpClientUtil.get(listDataSourceUrl);
        } catch (IOException e) {
            throw new OperatorException("Http get failed: " + e, e);
        }

        if (result != null) {
            try {
                DataSourceWrapperVo wrapperVo = jsonMapper.readValue(result, DataSourceWrapperVo.class);
                return wrapperVo == null ? null : wrapperVo.getResult();
            } catch (IOException ex) {
                throw new OperatorException("Parse result failed: " + ex, ex);
            }
        }

        return null;
    }

    private List<SingleMapVo> listSingleView() {
        String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
        String selectedDataSourceName = getParameterAsString(PARAMETER_DATA_SOURCE);
//        String listDataSourceUrl = "http://192.168.0.32:8080/api/datasources/list";
//        String selectedDataSourceName = "wuxianjiRT";
        ParameterTypeDynamicCategory dynamicCategory = (ParameterTypeDynamicCategory)(getParameters().getParameterType(selectedDataSourceName));
        String dataSourceValue = dynamicCategory.getCategoryValue(selectedDataSourceName);
        String listSingleMapUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_LIST_SINGLE_MAP) + dataSourceValue;

        String result;
        try {
            result = HttpClientUtil.get(listSingleMapUrl);
        } catch (IOException e) {
            throw new OperatorException("Http get failed: " + e, e);
        }

        if (result != null) {
            try {
                SingleMapWrapperVo wrapperVo = jsonMapper.readValue(result, SingleMapWrapperVo.class);
                return wrapperVo == null ? null : wrapperVo.getResult();
            } catch (IOException ex) {
                throw new OperatorException("Parse result failed: " + ex, ex);
            }
        }

        return null;
    }

    /**
     * {
     *   "result": [
     *        {
     *          "wuxianjiRT_total": 224058,
     *          "resultSet": [
     *             {
     *               "wuxianjiRT_total": 32185,
     *               "Province": "海南省"
     *             },
     *              ......
     *           ]
     *        }
     *     ],
     *   "code": 0
     * }
     */
    private List<Object> parseResult(String result) throws IOException {
        ObjectReader reader = jsonMapper.readerFor(DruidResultVo.class);
        DruidResultVo resultVo = reader.readValue(result);

        List<HashMap<String, Object>> resList = resultVo.getResult();
        if (resList != null && !resList.isEmpty()) {
            HashMap<String, Object> map = resList.get(0); // result列表仅有一条
            if (map != null && !map.isEmpty()) {
                Object resultSetObj = map.get("resultSet"); // 仅取key为"resultSet"的数据
                String resultSetStr = resultSetObj == null ? null : jsonMapper.writeValueAsString(resultSetObj);
                if (Objects.nonNull(resultSetStr)) {
                    List resultList = jsonMapper.readValue(resultSetStr, List.class);
                    return resultList;
                }
            }
        }

        return null;
    }

    private List<Attribute> getAttributes(List<Object> resultList) {
        List<Attribute> attributes = new ArrayList<>();

        if (resultList == null || resultList.isEmpty()) {
            return attributes;
        }

        try {
            // create table based on the first row
            Object firstRow = resultList.get(0);
            String resultStr = firstRow == null ? null : jsonMapper.writeValueAsString(firstRow);
            if (Objects.nonNull(resultStr)) {
                Map resultSetMap = jsonMapper.readValue(resultStr, Map.class);

                Iterator<String> keyIterator = resultSetMap.keySet().iterator();
                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    attributes.add(AttributeFactory.createAttribute(key, Ontology.STRING));
                }
            }
        } catch (IOException e) {
            return attributes;
        }

        return attributes;
    }

    private static class DataSourceWrapperVo {
        List<DataSourceVo> result;
        Integer code;

        public List<DataSourceVo> getResult() {
            return result;
        }

        public void setResult(List<DataSourceVo> result) {
            this.result = result;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    private static class DataSourceVo {
        String id;
        String name;
        String title;
        String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private static class SingleMapWrapperVo {
        List<SingleMapVo> result;
        Integer code;

        public List<SingleMapVo> getResult() {
            return result;
        }

        public void setResult(List<SingleMapVo> result) {
            this.result = result;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    private static class SingleMapVo {
        String id;
        String slice_name;
        String druid_datasource_id;
        String datasource_name;
        Object params;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSlice_name() {
            return slice_name;
        }

        public void setSlice_name(String slice_name) {
            this.slice_name = slice_name;
        }

        public String getDruid_datasource_id() {
            return druid_datasource_id;
        }

        public void setDruid_datasource_id(String druid_datasource_id) {
            this.druid_datasource_id = druid_datasource_id;
        }

        public String getDatasource_name() {
            return datasource_name;
        }

        public void setDatasource_name(String datasource_name) {
            this.datasource_name = datasource_name;
        }

        public Object getParams() {
            return params;
        }

        public void setParams(Object params) {
            this.params = params;
        }

    }

    private static class DruidResultVo {
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

    private static class SingleMapRequestVo {
        String druid_datasource_id;
        Object params;

        public String getDruid_datasource_id() {
            return druid_datasource_id;
        }

        public void setDruid_datasource_id(String druid_datasource_id) {
            this.druid_datasource_id = druid_datasource_id;
        }

        public Object getParams() {
            return params;
        }

        public void setParams(Object params) {
            this.params = params;
        }
    }

    public static void main(String[] args) {
        SingleViewExampleSource singleViewExampleSource = new SingleViewExampleSource();
//        singleViewExampleSource.listDataSource();
//        singleViewExampleSource.listSingleMap();
//        singleViewExampleSource.createExampleSet();
        boolean matched = singleViewExampleSource.isValidUrl("http://202.199.160.62:8080/validateCodeAction");
        System.out.println(matched);
    }
}
