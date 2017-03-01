package io.sugo.pio.operator.io;

import com.google.common.base.Strings;
import com.metamx.common.logger.Logger;
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
import io.sugo.pio.parameter.ParameterTypeDynamicCategory;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.util.*;

public class SingleViewExampleSource extends AbstractHttpExampleSource {

    private static final Logger logger = new Logger(SingleViewExampleSource.class);

    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_DATA_SOURCE = "data_source";

    public static final String PARAMETER_SINGLE_VIEW_DATA_SOURCE = "single_view_data_source";

    private static final String URI_LIST_DATA_SOURCE = "/datasources/list";

    private static final String URI_LIST_SINGLE_MAP = "/slices/list/";

    private static final String URI_DETAIL_SINGLE_MAP = "/slices/";

    private static final String URI_QUERY_DRUID = "/slices/query-druid";

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        if (isParameterExist(PARAMETER_URL) && isParameterExist(PARAMETER_DATA_SOURCE) &&
                isParameterExist(PARAMETER_SINGLE_VIEW_DATA_SOURCE)) {
            String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
            String druidUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_QUERY_DRUID);

            String singleViewValue = buildQueryDruidParam();
            String result = httpPost(druidUrl, singleViewValue);

            if (result != null) {
                List<Object> resultList;
                try {
                    resultList = parseResult(result);
                } catch (IOException e) {
                    logger.error("Parse druid result failed: ", e);
                    throw new OperatorException("Parse druid result failed: " + e, e);
                }

                logger.info("Get druid data from url '" + druidUrl + "' successfully.");

                DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, DataRowFactory.POINT_AS_DECIMAL_CHARACTER);
                List<Attribute> attributes = getAttributes(resultList);
                ExampleSetBuilder builder = ExampleSets.from(attributes);

                if (resultList != null && !resultList.isEmpty()) {
                    logger.info("Begin to traverse druid data to example set data. Data size:" + resultList.size());

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
                            logger.error("Parse druid data row failed, details:" + e.getMessage());
                            throw new OperatorException("Parse result failed: ", e);
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

                    logger.info("Traverse druid data to example set data successfully.");
                }

                return builder.build();
            }
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

    @Override
    public MetaData getGeneratedMetaData() throws OperatorException {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        if (isParameterExist(PARAMETER_URL) && isParameterExist(PARAMETER_DATA_SOURCE) &&
                isParameterExist(PARAMETER_SINGLE_VIEW_DATA_SOURCE)) {
            String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
            String druidUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_QUERY_DRUID);
            String singleViewValue = buildQueryDruidParam();

            String result = httpPost(druidUrl, singleViewValue);

            if (result != null) {
                List<Object> resultList;
                try {
                    resultList = parseResult(result);
                } catch (IOException e) {
                    logger.error("Parse metadata failed, details:" + e.getMessage());
                    throw new OperatorException("Parse result failed: " + e, e);
                }

                List<Attribute> attributes = getAttributes(resultList);
                if (attributes != null && !attributes.isEmpty()) {
                    attributes.forEach(attribute -> {
                        metaData.addAttribute(new AttributeMetaData(attribute));
                    });
                }

                logger.info("Dynamic generate single view metadata successfully.");
            }
        }

        return metaData;
    }

    /**
     * 动态获取单图下拉列表的值
     *
     * @return
     */
    private ParameterTypeDynamicCategory getSingleViewParamType() {
        if (isParameterExist(PARAMETER_DATA_SOURCE)) { // parameters不为空，且包含有值，再去取参数值，否则会死循环
            String dataSourceName = getParameterAsString(PARAMETER_DATA_SOURCE);
            if (!Strings.isNullOrEmpty(dataSourceName)) {
                List<SingleMapVo> singleMapList = listSingleView();
                if (singleMapList != null && !singleMapList.isEmpty()) {
                    ParameterTypeDynamicCategory.DynamicCategoryCombo[] categories =
                            new ParameterTypeDynamicCategory.DynamicCategoryCombo[singleMapList.size()];

                    for (int i = 0; i < singleMapList.size(); i++) {
                        SingleMapVo singleMap = singleMapList.get(i);

                        ParameterTypeDynamicCategory.DynamicCategoryCombo category =
                                new ParameterTypeDynamicCategory.DynamicCategoryCombo();

                        category.setId(singleMap.getId());
                        category.setName(singleMap.getSlice_name());
//                        category.setDesc(singleMap.getDatasource_name());
                        categories[i] = category;
                    }

                    logger.info("Dynamic generate single view parameter type successfully.");

                    return new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW_DATA_SOURCE, PARAMETER_DATA_SOURCE,
                            I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"),
                            categories, categories.length > 0 ? categories[0].getId() : null);
                }
            }
        }

        return new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW_DATA_SOURCE,
                PARAMETER_DATA_SOURCE,
                I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"));
    }

    /**
     * 动态获取数据源下拉列表的值
     *
     * @return
     */
    private ParameterTypeDynamicCategory getDataSourceParamType() {
        if (isParameterExist(PARAMETER_URL)) { // parameters不为空，且包含有值，再去取参数值，否则会死循环
            String url = getParameterAsString(PARAMETER_URL);
            if (isValidUrl(url)) {
                List<DataSourceVo> dataSourceList = listDataSource();
                if (dataSourceList != null && !dataSourceList.isEmpty()) {
                    ParameterTypeDynamicCategory.DynamicCategoryCombo[] categories =
                            new ParameterTypeDynamicCategory.DynamicCategoryCombo[dataSourceList.size()];

                    for (int i = 0; i < dataSourceList.size(); i++) {
                        DataSourceVo dataSource = dataSourceList.get(i);

                        ParameterTypeDynamicCategory.DynamicCategoryCombo category =
                                new ParameterTypeDynamicCategory.DynamicCategoryCombo();
                        category.setId(dataSource.getId());
                        category.setName(dataSource.getName());
                        category.setDesc(dataSource.getTitle());
                        categories[i] = category;
                    }

                    logger.info("Dynamic generate data source parameter type successfully.");

                    return new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, PARAMETER_URL,
                            I18N.getMessage("pio.SingleViewExampleSource.data_source"),
                            categories, categories.length > 0 ? categories[0].getId() : null);

                }
            }
        }

        return new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, PARAMETER_URL,
                I18N.getMessage("pio.SingleViewExampleSource.data_source"));
    }

    private String buildQueryDruidParam() {
        if (isParameterExist(PARAMETER_SINGLE_VIEW_DATA_SOURCE)) {
            String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
            String selectedSingleViewId = getParameterAsString(PARAMETER_SINGLE_VIEW_DATA_SOURCE);

            String singleMapUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_DETAIL_SINGLE_MAP) + selectedSingleViewId;
            SingleMapDetailWrapperVo singleMapDetailWrapper = httpGet(singleMapUrl, SingleMapDetailWrapperVo.class);

            if (singleMapDetailWrapper != null) {
                try {
                    SingleMapVo singleMapVo = singleMapDetailWrapper.getResult();
                    SingleMapRequestVo requestVo = new SingleMapRequestVo();
                    requestVo.setDruid_datasource_id(singleMapVo.getDruid_datasource_id());
                    requestVo.setParams(singleMapVo.getParams());
                    String requestStr = jsonMapper.writeValueAsString(requestVo);
                    return requestStr;
                } catch (IOException e) {
                    logger.error("Build druid query parameter failed, details:" + e.getMessage());
                    return null;
                }
            }
        }

        return null;
    }

    private List<DataSourceVo> listDataSource() {
        String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
        DataSourceWrapperVo wrapperVo = httpGet(listDataSourceUrl, DataSourceWrapperVo.class);

        return wrapperVo == null ? null : wrapperVo.getResult();
    }

    private List<SingleMapVo> listSingleView() {
        String listDataSourceUrl = getParameterAsString(PARAMETER_URL);
        String dataSourceValue = getParameterAsString(PARAMETER_DATA_SOURCE);

        String listSingleMapUrl = listDataSourceUrl.replaceAll(URI_LIST_DATA_SOURCE, URI_LIST_SINGLE_MAP) + dataSourceValue;
        SingleMapWrapperVo wrapperVo = httpGet(listSingleMapUrl, SingleMapWrapperVo.class);

        return wrapperVo == null ? null : wrapperVo.getResult();
    }

    /**
     * {
     * "result": [
     * {
     * "wuxianjiRT_total": 224058,
     * "resultSet": [
     * {
     * "wuxianjiRT_total": 32185,
     * "Province": "海南省"
     * },
     * ......
     * ]
     * }
     * ],
     * "code": 0
     * }
     */
    private List<Object> parseResult(String result) throws IOException {
        DruidResultVo resultVo = deserialize(result, DruidResultVo.class);

        if (resultVo != null) {
            List<HashMap<String, Object>> resList = resultVo.getResult();
            if (resList != null && !resList.isEmpty()) {
                HashMap<String, Object> map = resList.get(0); // only has one result
                if (map != null && !map.isEmpty()) {
                    Object resultSetObj = map.get("resultSet"); // only get key named "resultSet"
                    String resultSetStr = resultSetObj == null ? null : jsonMapper.writeValueAsString(resultSetObj);
                    if (Objects.nonNull(resultSetStr)) {
                        List resultList = jsonMapper.readValue(resultSetStr, List.class);
                        return resultList;
                    }
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

    private static class SingleMapDetailWrapperVo {
        SingleMapVo result;
        Integer code;

        public SingleMapVo getResult() {
            return result;
        }

        public void setResult(SingleMapVo result) {
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

}
