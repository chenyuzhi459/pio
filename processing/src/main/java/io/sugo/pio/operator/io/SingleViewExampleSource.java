package io.sugo.pio.operator.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import io.sugo.pio.parameter.ParameterTypeInt;
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

    public static final String PARAMETER_SINGLE_VIEW = "single_view";

    public static final String PARAMETER_PARAM = "param";

    public static final String PARAMETER_LIMIT = "limit";

    private static final String URI_QUERY_DRUID = "/api/slices/query-druid";

    private static final String URI_QUERY_DIMENSION = "/api/dimension";

    private static final String GROUP_BY_DEMENSION_SURFIX = "_GROUP";

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        String druidUrl = getParameterAsString(PARAMETER_URL) + URI_QUERY_DRUID;
        if (!druidUrl.startsWith("http")) {
            druidUrl = "http://" + druidUrl;
        }

        collectLog("Begin to get data from druid...");

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

            List<Attribute> dimensionAttrs = Lists.newArrayList();
            List<Attribute> metricAttrs = Lists.newArrayList();
            List<Attribute> allAttrs = obtainAttributes(dimensionAttrs, metricAttrs);

            ExampleSetBuilder builder = ExampleSets.from(allAttrs);
            if (resultList != null && !resultList.isEmpty()) {
                logger.info("Begin to traverse druid data to example set data. Data size:" + resultList.size());
                collectLog("Get data from druid successfully, data size: " + resultList.size());

                AttributeTree tree = buildTree(resultList, dimensionAttrs, new AttributeTree());
                collectLog("Build attribute tree finished.");

                buildExampleSet(tree, Lists.newArrayList(), allAttrs, factory, builder);
                logger.info("Traverse druid data to example set data successfully.");
            } else {
                collectLog("The data from druid is empty.");
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
        return OperatorGroup.dataSource;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.SingleViewExampleSource.description");
    }

    @Override
    public int getSequence() {
        return 3;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        ParameterTypeString urlType = new ParameterTypeString(PARAMETER_URL,
                I18N.getMessage("pio.SingleViewExampleSource.url"), false);
        urlType.setHidden(true);
        types.add(urlType);

        ParameterTypeDynamicCategory dataSourceType = new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, null,
                I18N.getMessage("pio.SingleViewExampleSource.data_source"),
                new String[0], null);
        types.add(dataSourceType);

        ParameterTypeDynamicCategory singleViewDataSourceType = new ParameterTypeDynamicCategory(PARAMETER_SINGLE_VIEW, null,
                I18N.getMessage("pio.SingleViewExampleSource.single_view_data_source"),
                new String[0], null);
        types.add(singleViewDataSourceType);

        ParameterTypeString param = new ParameterTypeString(PARAMETER_PARAM, "param", false);
        param.setHidden(true);
        types.add(param);

        ParameterTypeInt limit = new ParameterTypeInt(PARAMETER_LIMIT, I18N.getMessage("pio.SingleViewExampleSource.single_view_limit"),
                10, 1000000, 100);
        types.add(limit);

        return types;
    }

    @Override
    public MetaData getGeneratedMetaData() throws OperatorException {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        List<Attribute> attributes = obtainAttributes(Lists.newArrayList(), Lists.newArrayList());
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
        Integer limit = getParameterAsInt(PARAMETER_LIMIT);

        Preconditions.checkNotNull(dataSource, I18N.getMessage("pio.error.operator.data_source_can_not_null"));
        Preconditions.checkNotNull(param, I18N.getMessage("pio.error.operator.param_can_not_null"));

        try {

            ParamVo paramVo = deserialize(param, ParamVo.class);

            SingleMapRequestVo requestVo = new SingleMapRequestVo();
            requestVo.setDruid_datasource_id(dataSource);
            requestVo.setParams(paramVo);
            String requestStr = jsonMapper.writeValueAsString(requestVo);
            requestStr = requestStr.replaceAll("\"limit\":10", "\"limit\":" + limit);

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
                    return (List) resultSetObj;
                    /*String resultSetStr = resultSetObj == null ? null : jsonMapper.writeValueAsString(resultSetObj);
                    if (Objects.nonNull(resultSetStr)) {
                        List resultList = jsonMapper.readValue(resultSetStr, List.class);
                        return resultList;
                    }*/
                }
            }
        }

        return null;
    }

    private void buildExampleSet(AttributeTree tree, List<AttributeValuePair> breadcrumbs, List<Attribute> allAttrs, DataRowFactory factory, ExampleSetBuilder builder) {
        List<AttributeTree> subTrees = tree.getSubTrees();
        if (subTrees != null && !subTrees.isEmpty()) {
            List<AttributeValuePair> tempBreadcrumbs = new ArrayList<>(breadcrumbs);
            for (AttributeTree subTree : subTrees) {
                if (subTree instanceof AttributeLeaf) {
                    AttributeLeaf leaf = (AttributeLeaf) subTree;
                    String leafName = leaf.getAttributeName();
                    Attribute leafAttribute = getAttributeByName(allAttrs, leafName);

                    AttributeValuePair pair = new AttributeValuePair();
                    pair.setAttribute(leafAttribute);
                    pair.setValue(leaf.getAttributeValue());
                    tempBreadcrumbs.add(pair);

                    DataRow dataRow = factory.create(allAttrs.size());
                    // 1.dimensions
                    for (AttributeValuePair breadcrumb : tempBreadcrumbs) {
                        setDataRow(dataRow, breadcrumb.getAttribute(), breadcrumb.getValue());
                    }

                    // 2. metrics
                    Iterator keyIter = leaf.getMetricMap().keySet().iterator();
                    while (keyIter.hasNext()) {
                        String attrName = (String) keyIter.next();
                        Attribute attribute = getAttributeByName(allAttrs, attrName);
                        if (attribute != null) {
                            Object value = leaf.getMetricMap().get(attrName);
                            setDataRow(dataRow, attribute, value);
                        }
                    }

                    builder.addDataRow(dataRow);

                    // Take back current breadcrumb when function exit
                    tempBreadcrumbs.remove(pair);
                } else {
                    String attrName = subTree.getAttributeName();
                    Attribute attribute = getAttributeByName(allAttrs, attrName);

                    AttributeValuePair pair = new AttributeValuePair();
                    pair.setAttribute(attribute);
                    pair.setValue(subTree.getAttributeValue());
                    tempBreadcrumbs.add(pair);

                    buildExampleSet(subTree, tempBreadcrumbs, allAttrs, factory, builder);

                    // Take back current breadcrumb when function exit
                    tempBreadcrumbs.remove(pair);
                }
            }
        }
    }

    private AttributeTree buildTree(List values, List<Attribute> attributes, AttributeTree parentTree) {
        List<Attribute> tempAttrs = new ArrayList<>(attributes);
        if (tempAttrs.size() == 1) {
            List<AttributeTree> leaves = Lists.newArrayList();
            String attrName = tempAttrs.get(0).getName();
            if (values != null) {
                for (Object ele : values) {
                    AttributeLeaf leaf = new AttributeLeaf();
                    Map metricMap = (Map) ele;
                    leaf.setAttributeName(attrName);
                    leaf.setAttributeValue(metricMap.get(attrName));

                    metricMap.remove(attrName);
                    leaf.setMetricMap(metricMap);

                    leaves.add(leaf);
                }
            }
            parentTree.setSubTrees(leaves);
            return parentTree;
        }

        if (values.size() > 0) {
            int attrSize = tempAttrs.size();
            Map firstEle = (Map) values.get(0);

            // Find current layer attribute
            String currentLayerAttribute = null;
            for (int i = 0; i < attrSize; i++) {
                String attrName = tempAttrs.get(i).getName();
                if (firstEle.keySet().contains(attrName)) {
                    currentLayerAttribute = attrName;
                    tempAttrs.remove(i);
                    break;
                }
            }

            List<AttributeTree> subTrees = Lists.newArrayList();
            for (Object ele : values) {
                AttributeTree subTree = new AttributeTree();
                Map valueMap = (Map) ele;
                subTree.setAttributeName(currentLayerAttribute);
                subTree.setAttributeValue(valueMap.get(currentLayerAttribute));

                Iterator keyIter = valueMap.keySet().iterator();
                while (keyIter.hasNext()) {
                    String groupKey = (String) keyIter.next();
                    if (groupKey.endsWith(GROUP_BY_DEMENSION_SURFIX) &&
                            valueMap.get(groupKey) instanceof List) {
                        // Recursive build tree
                        buildTree((List) valueMap.get(groupKey), tempAttrs, subTree);
                    }
                }

                subTrees.add(subTree);
                parentTree.setSubTrees(subTrees);
            }
        }

        return parentTree;
    }

    private void setDataRow(DataRow dataRow, Attribute attribute, Object attrValue) {
        int valueType = attribute.getValueType();

        if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
            String attrValueStr = attrValue == null ? null : attrValue.toString();
            double value = attribute.getMapping().mapString(attrValueStr);
            dataRow.set(attribute, value);
        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NUMERICAL)) {
            double value;
            if (attrValue == null || Strings.isNullOrEmpty(attrValue.toString())) {
                value = 0.0D / 0.0;
            } else {
                value = Double.valueOf(attrValue.toString());
            }
            dataRow.set(attribute, value);
        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.DATE_TIME)) {
            // TODO: parse datetime value
            double value = 0.0D / 0.0;
            dataRow.set(attribute, value);
        } else {

        }
    }

    private Attribute getAttributeByName(List<Attribute> attributes, String attrName) {
        if (!attributes.isEmpty()) {
            for (Attribute attribute : attributes) {
                if (attribute.getName().equals(attrName)) {
                    return attribute;
                }
            }
        }

        return null;
    }

    private List<Attribute> obtainAttributes(final List<Attribute> dimensionAttrs, final List<Attribute> metricAttrs) {
        List<Attribute> allAttrs = Lists.newArrayList();
        String dataSource = getParameterAsString(PARAMETER_DATA_SOURCE);
        String param = getParameterAsString(PARAMETER_PARAM);
        String url = getParameterAsString(PARAMETER_URL);
        if (!Strings.isNullOrEmpty(url) && !url.startsWith("http")) {
            url = "http://" + url;
        }

        if (!Strings.isNullOrEmpty(dataSource) && !Strings.isNullOrEmpty(param)) {
            ParamVo paramVo = deserialize(param, ParamVo.class);
            if (paramVo != null) {
                String dimensionQueryStr = null;
                try {
                    DimensionQueryVo dimensionQueryVo = new DimensionQueryVo();
                    dimensionQueryVo.setParentId(dataSource);

                    DimensionQueryNameVo queryNameVo = new DimensionQueryNameVo();
                    queryNameVo.set$in(paramVo.getDimensions());
                    dimensionQueryVo.setName(queryNameVo);

                    dimensionQueryStr = jsonMapper.writeValueAsString(dimensionQueryVo);
                } catch (JsonProcessingException ignore) {
                }

                String dimensionUrl = url + URI_QUERY_DIMENSION;
                String result = httpPost(dimensionUrl, dimensionQueryStr);
                List<DimensionVo> dimensionList = deserialize2list(result, DimensionVo.class);

                // 1.dimensions
                if (dimensionList != null) {
                    dimensionList.forEach(dimensionVo -> {
                        String name = dimensionVo.getName();
                        String type = dimensionVo.getType();
                        dimensionAttrs.add(AttributeFactory.createAttribute(name, convertType(type)));
                    });
                }

                // 2.metrics
                List<String> metrics = paramVo.getMetrics();
                if (metrics != null) {
                    metrics.forEach(metric -> {
                        metricAttrs.add(AttributeFactory.createAttribute(metric, Ontology.NUMERICAL));
                    });
                }
            }
        }

        allAttrs.addAll(dimensionAttrs);
        allAttrs.addAll(metricAttrs);

        return allAttrs;
    }

    private int convertType(String dimensionType) {
        switch (dimensionType) {
            case "String":
                return Ontology.STRING;
            case "Int":
            case "Long":
            case "Float":
                return Ontology.NUMERICAL;
            case "Date":
                return Ontology.DATE_TIME;
            default:
                return Ontology.STRING;
        }
    }

    private static class DimensionQueryVo {
        String parentId;
        DimensionQueryNameVo name;

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public DimensionQueryNameVo getName() {
            return name;
        }

        public void setName(DimensionQueryNameVo name) {
            this.name = name;
        }
    }

    private static class DimensionQueryNameVo {
        List<String> $in;

        public List<String> get$in() {
            return $in;
        }

        public void set$in(List<String> $in) {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ParamVo {
        List<Object> filters;
        List<String> metrics;
        String vizType;
        Object timezone;
        List<String> dimensions;
        Object dimensionExtraSettingDict;
        String selectedDataSourceId;

        public List<Object> getFilters() {
            return filters;
        }

        public void setFilters(List<Object> filters) {
            this.filters = filters;
        }

        public List<String> getMetrics() {
            return metrics;
        }

        public void setMetrics(List<String> metrics) {
            this.metrics = metrics;
        }

        public String getVizType() {
            return vizType;
        }

        public void setVizType(String vizType) {
            this.vizType = vizType;
        }

        public Object getTimezone() {
            return timezone;
        }

        public void setTimezone(Object timezone) {
            this.timezone = timezone;
        }

        public List<String> getDimensions() {
            return dimensions;
        }

        public void setDimensions(List<String> dimensions) {
            this.dimensions = dimensions;
        }

        public Object getDimensionExtraSettingDict() {
            return dimensionExtraSettingDict;
        }

        public void setDimensionExtraSettingDict(Object dimensionExtraSettingDict) {
            this.dimensionExtraSettingDict = dimensionExtraSettingDict;
        }

        public String getSelectedDataSourceId() {
            return selectedDataSourceId;
        }

        public void setSelectedDataSourceId(String selectedDataSourceId) {
            this.selectedDataSourceId = selectedDataSourceId;
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
        ParamVo params;

        public String getDruid_datasource_id() {
            return druid_datasource_id;
        }

        public void setDruid_datasource_id(String druid_datasource_id) {
            this.druid_datasource_id = druid_datasource_id;
        }

        public ParamVo getParams() {
            return params;
        }

        public void setParams(ParamVo params) {
            this.params = params;
        }

    }

    private static class AttributeValuePair {
        Attribute attribute;
        Object value;

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private static class AttributeTree {
        String attributeName;
        Object attributeValue;
        List<AttributeTree> subTrees;

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public Object getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(Object attributeValue) {
            this.attributeValue = attributeValue;
        }

        public List<AttributeTree> getSubTrees() {
            return subTrees;
        }

        public void setSubTrees(List<AttributeTree> subTrees) {
            this.subTrees = subTrees;
        }
    }

    private static class AttributeLeaf extends AttributeTree {
        Map<String, Object> metricMap;

        public Map<String, Object> getMetricMap() {
            return metricMap;
        }

        public void setMetricMap(Map<String, Object> metricMap) {
            this.metricMap = metricMap;
        }
    }

}
