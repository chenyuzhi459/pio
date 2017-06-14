package io.sugo.pio.operator.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.config.RuntimeConfig;
import io.sugo.pio.common.utils.JsonObjectIterator;
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
import io.sugo.pio.tools.AttributeSubsetSelector;
import io.sugo.pio.tools.Ontology;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 */
public class ScanExampleSource extends AbstractHttpExampleSource {
    private static final Logger logger = new Logger(ScanExampleSource.class);

    private static final int BATCH_SIZE = 20000;

    public static final String PARAMETER_DATA_SOURCE_NAME = "data_source_name";

    public static final String PARAMETER_INTERVALS = "intervals";

    public static final String PARAMETER_LIMIT = "limit";

    public static final String PARAMETER_COLUMNS = "columns";

    private static final String druidUrl = RuntimeConfig.get("pio.broker.data.fetcher.url");

    private final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getOutputPort());

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        String dataSource = getParameterAsString(PARAMETER_DATA_SOURCE_NAME);
        String intervals = getParameterAsString(PARAMETER_INTERVALS);
        String columns = getParameterAsString(PARAMETER_COLUMNS);
        int limit = getParameterAsInt(PARAMETER_LIMIT);

        List<String> selectedColumns = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(columns)) {
            String[] sc = columns.split(";");
            selectedColumns = Arrays.asList(sc);
        }
        ScanQuery query = new ScanQuery();
        query.setDataSource(dataSource);
        query.setLimit(limit);
        query.setColumns(selectedColumns);
        query.setIntervals(ImmutableList.of(intervals));

        String queryStr;
        try {
            queryStr = jsonMapper.writeValueAsString(query);
        } catch (IOException e) {
            throw new OperatorException("pio.error.parsing.unresolvable_druid_request", query, e);
        }

        InputStream stream = streamHttpPost(druidUrl, queryStr);
        JsonObjectIterator iterator = new JsonObjectIterator(stream);
        DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, DataRowFactory.POINT_AS_DECIMAL_CHARACTER);

        List<Attribute> attributes = getSelectedAttributes(selectedColumns);
        ExampleSetBuilder builder = ExampleSets.from(attributes);
        int attrSize = attributes.size();

        while (iterator.hasNext()) {
            HashMap resultValue = iterator.next();
            if (resultValue != null) {
                List<List<Object>> eventRows = (List<List<Object>>) resultValue.get("events");
                for (List<Object> row : eventRows) {
                    DataRow dataRow = factory.create(attrSize);
                    for (int i = 0; i < attrSize; i++) {
                        Attribute attr = attributes.get(i);
                        int valueType = attr.getValueType();
                        Object attrValue = row.get(i + 1);

                        if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
                            String attrValueStr = attrValue == null ? null : attrValue.toString();
                            double value = attr.getMapping().mapString(attrValueStr);
                            dataRow.set(attr, value);
                        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NUMERICAL)) {
                            double value;
                            if (attrValue == null || Strings.isNullOrEmpty(attrValue.toString())) {
                                value = 0.0D / 0.0;
                            } else {
                                value = Double.valueOf(attrValue.toString());
                            }
                            dataRow.set(attr, value);
                        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.DATE_TIME)) {
                            double value;
                            if (attrValue == null || Strings.isNullOrEmpty(attrValue.toString())) {
                                value = 0.0D / 0.0;
                            } else {
                                value = new DateTime(attrValue).getMillis();
                            }
                            dataRow.set(attr, value);
                        }
                    }
                    builder.addDataRow(dataRow);
                }
            }
        }

        return builder.build();
    }

    @Override
    public MetaData getGeneratedMetaData() throws OperatorException {
        return getScanMetaData();
    }

    private List<Attribute> getSelectedAttributes(List<String> selectedColumns) {
        ExampleSetMetaData metaData = getOutputPort().getMetaData() == null ? null :
                (ExampleSetMetaData) getOutputPort().getMetaData();
        List<Attribute> selectedAttributes = Lists.newArrayList();
        if (metaData != null) {
            for (String selectedColumn : selectedColumns) {
                AttributeMetaData attrMetaData = metaData.getAttributeByName(selectedColumn);
                if (attrMetaData != null) {
                    Attribute attribute = AttributeFactory.createAttribute(attrMetaData.getName(), attrMetaData.getValueType());
                    selectedAttributes.add(attribute);
                }
            }
        }

        return selectedAttributes;
    }

    private MetaData getScanMetaData() {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        String dataSource = getParameterAsString(PARAMETER_DATA_SOURCE_NAME);
//        dataSource = "wuxianjiRT";
        if (!Strings.isNullOrEmpty(dataSource) && !Strings.isNullOrEmpty(dataSource)) {
            MetadataQuery metadataQuery = new MetadataQuery();
            metadataQuery.setDataSource(dataSource);

            String queryStr = null;
            try {
                queryStr = jsonMapper.writeValueAsString(metadataQuery);
            } catch (JsonProcessingException ignore) {
            }

            String result = httpPost(druidUrl, queryStr);
            List<MetadataInfo> metadataInfoList = deserialize2list(result, MetadataInfo.class);

            if (metadataInfoList != null && !metadataInfoList.isEmpty()) {
                MetadataInfo metadataInfo = metadataInfoList.get(0);
                Map<String, ColumnInfo> columnInfoMap = metadataInfo.getColumns();
                Iterator keyIter = columnInfoMap.keySet().iterator();
                while (keyIter.hasNext()) {
                    String columnName = (String) keyIter.next();
                    String columnType = columnInfoMap.get(columnName).getType();
                    Attribute attribute = AttributeFactory.createAttribute(columnName, convertType(columnType));
                    metaData.addAttribute(new AttributeMetaData(attribute));
                }
            }

            logger.info("Dynamic get scan query metadata successfully.");
        }

        return metaData;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.ScanExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.dataSource;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.ScanExampleSource.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        ParameterTypeDynamicCategory dataSourceName = new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE_NAME, null,
                I18N.getMessage("pio.ScanExampleSource.data_source_name"),
                new String[0], null);
        types.add(dataSourceName);

        ParameterTypeInt limit = new ParameterTypeInt(PARAMETER_LIMIT, I18N.getMessage("pio.ScanExampleSource.limit"), 10000, 2000000, 10000);
        types.add(limit);

        ParameterTypeString intervals = new ParameterTypeString(PARAMETER_INTERVALS,
                I18N.getMessage("pio.ScanExampleSource.interval"), "1000/3000", false);
        types.add(intervals);

        types.addAll(attributeSelector.getSubsetAttributeFilterParamTypes(PARAMETER_COLUMNS, I18N.getMessage("pio.ScanExampleSource.columns")));

        return types;
    }

    private static class ScanQuery {
        private String queryType = "lucene_scan";
        private String dataSource;
        private String resultFormat = "compactedList";
        private int batchSize = BATCH_SIZE;
        private int limit;
        private List<String> columns = new ArrayList<>();
        private List<String> intervals = new ArrayList<>();
        private String filter = "*:*";

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getResultFormat() {
            return resultFormat;
        }

        public void setResultFormat(String resultFormat) {
            this.resultFormat = resultFormat;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List<String> getColumns() {
            return columns;
        }

        public void setColumns(List<String> columns) {
            this.columns = columns;
        }

        public List<String> getIntervals() {
            return intervals;
        }

        public void setIntervals(List<String> intervals) {
            this.intervals = intervals;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }
    }

    private static class MetadataQuery {
        private String queryType = "lucene_segmentMetadata";
        private String dataSource;
        private boolean merge = true;

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public boolean isMerge() {
            return merge;
        }

        public void setMerge(boolean merge) {
            this.merge = merge;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MetadataInfo {
        String id;
        Long size;
        Long numRows;
        Map<String, ColumnInfo> columns;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public Long getNumRows() {
            return numRows;
        }

        public void setNumRows(Long numRows) {
            this.numRows = numRows;
        }

        public Map<String, ColumnInfo> getColumns() {
            return columns;
        }

        public void setColumns(Map<String, ColumnInfo> columns) {
            this.columns = columns;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ColumnInfo {
        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
