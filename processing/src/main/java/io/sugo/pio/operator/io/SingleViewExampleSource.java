package io.sugo.pio.operator.io;

import com.fasterxml.jackson.core.JsonProcessingException;
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

//    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_DATA_SOURCE = "data_source";

    public static final String PARAMETER_SINGLE_VIEW_DATA_SOURCE = "single_view_data_source";

    public static final String PARAMETER_PARAM = "param";

    private static final String SINGLE_VIEW_URL_PREFIX = "http://192.168.0.212:8000/api";

    private static final String URI_LIST_DATA_SOURCE = "/datasources/list";

    private static final String URI_LIST_SINGLE_MAP = "/slices/list/";

    private static final String URI_DETAIL_SINGLE_MAP = "/slices/";

    private static final String URI_QUERY_DRUID = "/slices/query-druid";

    private static final String URI_QUERY_DIMENSION = "/dimension";

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        String druidUrl = SINGLE_VIEW_URL_PREFIX + URI_QUERY_DRUID;

        String singleViewValue = buildQueryDruidParam();
        String result = httpPost(druidUrl, singleViewValue);

        if (result != null) {
            List<Object> resultList;
            try {
                resultList = parseResult(result);
            } catch (IOException e) {
                throw new OperatorException("pio.error.parsing.unresolvable_druid_result", result, e);
            }

            logger.info("Get druid data from url '" + druidUrl + "' successfully.");

            DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, DataRowFactory.POINT_AS_DECIMAL_CHARACTER);
            List<Attribute> attributes = getAttributes();
            ExampleSetBuilder builder = ExampleSets.from(attributes);

            if (resultList != null && !resultList.isEmpty()) {
                logger.info("Begin to traverse druid data to example set data. Data size:" + resultList.size());

                int attrSize = attributes.size();

                // traverse all rows to store data
                for (Object resultObj : resultList) {
                    String resultStr = null;
                    Map resultMap = null;
                    try {
                        resultStr = jsonMapper.writeValueAsString(resultObj);
                        if (Objects.nonNull(resultStr)) {
                            resultMap = jsonMapper.readValue(resultStr, Map.class);
                        }
                    } catch (IOException e) {
                        throw new OperatorException("pio.error.parsing.unresolvable_druid_row", resultStr, e);
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
        /*ParameterTypeString urlType = new ParameterTypeString(PARAMETER_URL,
                I18N.getMessage("pio.SingleViewExampleSource.url"), false);
        types.add(urlType);*/

        ParameterTypeDynamicCategory dataSourceType = new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, null,
                I18N.getMessage("pio.SingleViewExampleSource.data_source"),
                new String[0], null);
        types.add(dataSourceType);

        ParameterTypeDynamicCategory singleViewDataSourceType = new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW_DATA_SOURCE, null,
                I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"),
                new String[0], null);
        types.add(singleViewDataSourceType);

        ParameterTypeString param = new ParameterTypeString(PARAMETER_PARAM, "param", false);
        param.setHidden(true);
        types.add(param);

        return types;
    }

    @Override
    public MetaData getGeneratedMetaData() throws OperatorException {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        List<Attribute> attributes = getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            attributes.forEach(attribute -> {
                metaData.addAttribute(new AttributeMetaData(attribute));
            });
        }

        logger.info("Dynamic generate single view metadata successfully.");

        return metaData;
    }

    private String buildQueryDruidParam() {
        String dataSource = getParameterAsString(PARAMETER_DATA_SOURCE);
        String param = getParameterAsString(PARAMETER_PARAM);

//        Preconditions.checkNotNull(dataSource, I18N.getMessage("pio.error.operator.data_source_can_not_null"));
//        Preconditions.checkNotNull(param, I18N.getMessage("pio.error.operator.param_can_not_null"));

        try {
            SingleMapRequestVo requestVo = new SingleMapRequestVo();
            requestVo.setDruid_datasource_id(dataSource);
            requestVo.setParams(param);
            String requestStr = jsonMapper.writeValueAsString(requestVo);
            return requestStr;
        } catch (IOException e) {
            logger.error("Build druid query parameter failed, details:" + e.getMessage());
            return null;
        }
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

    private List<Attribute> getAttributes() {
        List<Attribute> attributes = new ArrayList<>();
//        String dataSource = getParameterAsString(PARAMETER_DATA_SOURCE);
//        String param = getParameterAsString(PARAMETER_PARAM);
        String dataSource = "SJ9o92XGl";
        String param = "{\"filters\":[{\"eq\":\"-7 days\",\"op\":\"in\",\"col\":\"__time\"}],\"metrics\":[\"wuxianjiRT_total\"],\"vizType\":\"multi_dim_line\",\"dimensions\":[\"Province\",\"__time\"],\"dimensionExtraSettingDict\":{\"__time\":{\"sortCol\":\"wuxianjiRT_total\",\"sortDirect\":\"desc\",\"granularity\":\"P1D\"},\"Province\":{\"sortCol\":\"wuxianjiRT_total\",\"sortDirect\":\"desc\"}}}";

        if (!Strings.isNullOrEmpty(dataSource) && !Strings.isNullOrEmpty(param)) {
            ParamVo paramVo = deserialize(param, ParamVo.class);
            if (paramVo != null) {
                Object dimensions = paramVo.getDimensions();

                DimensionQueryVo dimensionQueryVo = new DimensionQueryVo();
                dimensionQueryVo.setParentId(dataSource);
                dimensionQueryVo.setName(dimensions.toString());

                String dimensionQueryStr = null;
                try {
                    dimensionQueryStr = jsonMapper.writeValueAsString(dimensionQueryVo);
                } catch (JsonProcessingException ignore) {
                }

                String dimensionUrl = SINGLE_VIEW_URL_PREFIX + URI_QUERY_DIMENSION;
                String result = httpPost(dimensionUrl, dimensionQueryStr);
                List<DimensionVo> dimensionList = deserialize2list(result, DimensionVo.class);
                if (dimensionList != null) {
                    dimensionList.forEach(dimensionVo -> {
                        String name = dimensionVo.getName();
                        String type = dimensionVo.getType();
                        attributes.add(AttributeFactory.createAttribute(name, convertType(type)));
                    });
                }
            }
        }

        return attributes;
    }

    private int convertType(String dimensionType) {
        switch (dimensionType) {
            case "String":
                return Ontology.STRING;
            default:
                return Ontology.STRING;
        }
    }

    /*private static class DataSourceWrapperVo {
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
    }*/

    private static class DimensionQueryVo {
        String parentId;
        String name;

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            DimensionQueryNameVo dimensionName = new DimensionQueryNameVo();
            dimensionName.set$in(name);
            try {
                this.name = jsonMapper.writeValueAsString(dimensionName);
            } catch (JsonProcessingException ignore) {
            }
        }
    }

    private static class DimensionQueryNameVo {
        String $in;

        public String get$in() {
            return $in;
        }

        public void set$in(String $in) {
            this.$in = $in;
        }
    }

    private static class DimensionVo {
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

    private static class ParamVo {
        Object filters;
        Object metrics;
        Object vizType;
        Object timezone;
        Object dimensions;
        Object dimensionExtraSettingDict;

        public Object getFilters() {
            return filters;
        }

        public void setFilters(Object filters) {
            this.filters = filters;
        }

        public Object getMetrics() {
            return metrics;
        }

        public void setMetrics(Object metrics) {
            this.metrics = metrics;
        }

        public Object getVizType() {
            return vizType;
        }

        public void setVizType(Object vizType) {
            this.vizType = vizType;
        }

        public Object getTimezone() {
            return timezone;
        }

        public void setTimezone(Object timezone) {
            this.timezone = timezone;
        }

        public Object getDimensions() {
            return dimensions;
        }

        public void setDimensions(Object dimensions) {
            this.dimensions = dimensions;
        }

        public Object getDimensionExtraSettingDict() {
            return dimensionExtraSettingDict;
        }

        public void setDimensionExtraSettingDict(Object dimensionExtraSettingDict) {
            this.dimensionExtraSettingDict = dimensionExtraSettingDict;
        }
    }

    /*private static class SingleMapWrapperVo {
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

    }*/

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
        SingleViewExampleSource instance = new SingleViewExampleSource();
        instance.getAttributes();
    }


}
