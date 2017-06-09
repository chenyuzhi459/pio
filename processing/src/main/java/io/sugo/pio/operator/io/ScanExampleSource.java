package io.sugo.pio.operator.io;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.JsonObjectIterator;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 */
public class ScanExampleSource extends AbstractHttpExampleSource {
    private static final Logger logger = new Logger(ScanExampleSource.class);

    public static final String PARAMETER_URL = "url";

    public static final String PARAMETER_DATA_SOURCE = "data_source";

    public static final String PARAMETER_INTERVALS = "intervals";

    public static final String PARAMETER_BATCH_SIZE = "batch_size";

    public static final String PARAMETER_LIMIT = "limit";

    public static final String PARAMETER_META_DATA = "data_set_meta_data_information";
    public static final String PARAMETER_COLUMN_INDEX = "column_index";
    public static final String PARAMETER_COLUMN_META_DATA = "attribute_meta_data_information";
    public static final String PARAMETER_COLUMN_NAME = "attribute name";
    public static final String PARAMETER_COLUMN_SELECTED = "column_selected";
    public static final String PARAMETER_COLUMN_VALUE_TYPE = "attribute_value_type";
    public static final String PARAMETER_COLUMN_ROLE = "attribute_role";

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        String druidUrl = getParameterAsString(PARAMETER_URL);
        String datasource = getParameterAsString(PARAMETER_DATA_SOURCE);
        String intervals = getParameterAsString(PARAMETER_INTERVALS);
        int batchSize = getParameterAsInt(PARAMETER_BATCH_SIZE);
        int limit = getParameterAsInt(PARAMETER_LIMIT);
        List<Attribute> attributes = getAttributes();
        if(attributes.isEmpty()) {
            return null;
        }

        List<String> columns = Lists.transform(attributes, new Function<Attribute, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Attribute input) {
                return input.getName();
            }
        });
        ScanQuery query = new ScanQuery();
        query.setDataSource(datasource);
        query.setBatchSize(batchSize);
        query.setLimit(limit);
        query.setColumns(columns);
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
                        Object attrValue = row.get(i+1);

                        if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
                            String attrValueStr = attrValue == null ? null : attrValue.toString();
                            double value = attr.getMapping().mapString(attrValueStr);
                            dataRow.set(attr, value);
                        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NUMERICAL)) {
                            double value;
                            if (attrValue == null || Strings.isNullOrEmpty(attrValue.toString()))  {
                                value = 0.0D / 0.0;
                            } else {
                                value = Double.valueOf(attrValue.toString());
                            }
                            dataRow.set(attr, value);
                        } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.DATE_TIME)) {
                            // TODO: parse datetime value
                        } else {

                        }
                    }
                    builder.addDataRow(dataRow);
                }
            }
        }

        return builder.build();
    }

    private List<Attribute> getAttributes() {
        List<String[]> metaDataSettings;
        if (isParameterSet(PARAMETER_META_DATA)) {
            try {
                metaDataSettings = getParameterList(PARAMETER_META_DATA);
            } catch (UndefinedParameterError e) {
                metaDataSettings = Collections.emptyList();
            }
        } else {
            metaDataSettings = Collections.emptyList();
        }

        // find largest used column index
        int maxUsedColumnIndex = -1;
        for (String[] metaDataDefinition : metaDataSettings) {
            int columnIndex = Integer.parseInt(metaDataDefinition[0]);
            maxUsedColumnIndex = Math.max(maxUsedColumnIndex, columnIndex);
        }
        // initialize with values from settings
        List<Attribute> attributes = new ArrayList<>(maxUsedColumnIndex + 2);
//        attributes.add(AttributeFactory.createAttribute("timestamp", Ontology.DATE_TIME));

        for (String[] metaDataDefinition : metaDataSettings) {
            String[] metaDataDefintionValues = ParameterTypeTupel.transformString2Tupel(metaDataDefinition[1]);
            boolean isSelected = Boolean.parseBoolean(metaDataDefintionValues[1]);
            if (isSelected) { // otherwise details don't matter
                String name = metaDataDefintionValues[0].trim();

                int valueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(metaDataDefintionValues[2]);
                // fallback for old processes where attribute value type was saved as index
                // rather than as string
                try {
                    if (valueType == -1) {
                        valueType = Integer.parseInt(metaDataDefintionValues[2]);
                    }
                    attributes.add(AttributeFactory.createAttribute(name, valueType));
                } catch (Exception ignore) {}
            }
        }

        return attributes;
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
        ParameterTypeString urlType = new ParameterTypeString(PARAMETER_URL,
                I18N.getMessage("pio.ScanExampleSource.url"), false);
        urlType.setHidden(true);
        types.add(urlType);

        ParameterTypeDynamicCategory dataSourceType = new ParameterTypeDynamicCategory(PARAMETER_DATA_SOURCE, null,
                I18N.getMessage("pio.ScanExampleSource.data_source"),
                new String[0], null);
        types.add(dataSourceType);

        ParameterTypeInt batchSize = new ParameterTypeInt(PARAMETER_BATCH_SIZE,
                I18N.getMessage("pio.ScanExampleSource.batch_size"), 5000, 20000, 5000);
        types.add(batchSize);

        ParameterTypeInt limit = new ParameterTypeInt(PARAMETER_LIMIT, I18N.getMessage("pio.ScanExampleSource.limit"), 10000, 2000000, 10000);
        types.add(limit);

        ParameterTypeString intervals = new ParameterTypeString(PARAMETER_INTERVALS,
                I18N.getMessage("pio.ScanExampleSource.interval"), "1000/3000", false);
        types.add(intervals);

        ParameterTypeList attributes = new ParameterTypeList(PARAMETER_META_DATA, "The meta data information", //
                new ParameterTypeInt(PARAMETER_COLUMN_INDEX, "The column index", 0, Integer.MAX_VALUE), //
                new ParameterTypeTupel(PARAMETER_COLUMN_META_DATA, "The meta data definition of one column", //
                        new ParameterTypeString(PARAMETER_COLUMN_NAME, "Describes the attributes name.", ""), //
                        new ParameterTypeBoolean(PARAMETER_COLUMN_SELECTED, "Indicates if a column is selected", true), //
                        new ParameterTypeStringCategory(PARAMETER_COLUMN_VALUE_TYPE, "Indicates the value type of an attribute",
                                Ontology.VALUE_TYPE_NAMES_VALUE, Ontology.VALUE_TYPE_NAMES, "attribute_value", true), //
                        new ParameterTypeStringCategory(PARAMETER_COLUMN_ROLE, "Indicates the role of an attribute",
                                Attributes.KNOWN_ATTRIBUTE_TYPES_VALUE, Attributes.KNOWN_ATTRIBUTE_TYPES, Attributes.ATTRIBUTE_NAME, true)));
        types.add(attributes);

        return types;
    }

    private static class ScanQuery {
        private String queryType = "lucene_scan";
        private String dataSource;
        private String resultFormat = "compactedList";
        private int batchSize;
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
}
