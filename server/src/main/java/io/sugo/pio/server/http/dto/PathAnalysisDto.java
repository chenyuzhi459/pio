package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class PathAnalysisDto {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @JsonProperty
    private String dataSource;
    @JsonProperty
    private ColumnName dimension;
    @JsonProperty
    private List<String> pages;
    @JsonProperty
    private List<FilterDimension> filters;
    @JsonProperty
    private String homePage;

    /**
     * yyyy-mm-dd
     */
    @JsonProperty
    private String startDate;

    /**
     * yyyy-mm-dd
     */
    @JsonProperty
    private String endDate;

    private static class ColumnName {
        @JsonProperty
        String sessionId;
        @JsonProperty
        String pageName;
        @JsonProperty
        String date;
        @JsonProperty
        String userId;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getPageName() {
            return pageName;
        }

        public void setPageName(String pageName) {
            this.pageName = pageName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ColumnName getDimension() {
        return dimension;
    }

    public void setDimension(ColumnName dimension) {
        this.dimension = dimension;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public List<FilterDimension> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDimension> filters) {
        this.filters = filters;
    }

    public String buildQuery() {
        Query query = new Query();
        query.setDataSource(this.dataSource);

        // Set filters
        InField inField = new InField();
        inField.setDimension(this.getDimension().getPageName());
        inField.setValues(this.pages);

        BetweenEqualField boundField = new BetweenEqualField();
        boundField.setDimension(this.getDimension().getDate());
        boundField.setLower(this.startDate);
        boundField.setUpper(this.endDate);

        query.getFilter().getFields().add(inField);
        query.getFilter().getFields().add(boundField);

        if (this.filters != null && this.filters.size() > 0) {
            query.getFilter().getFields().addAll(buildFilterFieldsd(this.filters));
        }

        // Set dimensions
        query.getDimensions().get(0).setDimension(this.getDimension().getSessionId());
        query.getDimensions().get(0).setOutputName("sessionId");
        query.getDimensions().get(1).setDimension(this.getDimension().getPageName());
        query.getDimensions().get(1).setOutputName("pageName");
        query.getDimensions().get(2).setDimension(this.getDimension().getDate());
        query.getDimensions().get(2).setOutputName("accessTime");

        String userId = this.getDimension().getUserId();
        if (userId != null && !userId.isEmpty()) {
            Dimension userIdDimension = new Dimension();
            userIdDimension.setDimension(userId);
            userIdDimension.setOutputName("userId");
            query.getDimensions().add(userIdDimension);
        }

        String queryStr = "";
        try {
            queryStr = jsonMapper.writeValueAsString(query);
        } catch (JsonProcessingException ignore) {
        }

        return queryStr;
    }

    private List<FieldType> buildFilterFieldsd(List<FilterDimension> filters) {
        List<FieldType> fields = new ArrayList<>(filters.size());
        for (FilterDimension filter : filters) {
            switch (filter.getAction()) {
                case "=":
                    EqualField equalField = new EqualField();
                    equalField.setDimension(filter.getDimension());
                    equalField.setValue(filter.getValue().toString());
                    fields.add(equalField);
                    break;
                case "!=":
                    NotEqualField notEqualField = new NotEqualField();
                    notEqualField.getField().setDimension(filter.getDimension());
                    notEqualField.getField().setValue(filter.getValue().toString());
                    fields.add(notEqualField);
                    break;
                case ">":
                    GreaterThanField greaterThanField = new GreaterThanField();
                    greaterThanField.setDimension(filter.getDimension());
                    greaterThanField.setLower(filter.getValue().toString());
                    fields.add(greaterThanField);
                    break;
                case "<":
                    LessThanField lessThanField = new LessThanField();
                    lessThanField.setDimension(filter.getDimension());
                    lessThanField.setUpper(filter.getValue().toString());
                    fields.add(lessThanField);
                    break;
                case ">=":
                    GreaterThanEqualField greaterThanEqualField = new GreaterThanEqualField();
                    greaterThanEqualField.setDimension(filter.getDimension());
                    greaterThanEqualField.setLower(filter.getValue().toString());
                    fields.add(greaterThanEqualField);
                    break;
                case "<=":
                    LessThanEqualField lessThanEqualField = new LessThanEqualField();
                    lessThanEqualField.setDimension(filter.getDimension());
                    lessThanEqualField.setUpper(filter.getValue().toString());
                    fields.add(lessThanEqualField);
                    break;
                case "between":
                    List<String> valuePair = (List) filter.getValue();
                    BetweenField betweenField = new BetweenField();
                    betweenField.setDimension(filter.getDimension());
                    betweenField.setLower(valuePair.get(0));
                    betweenField.setUpper(valuePair.get(1));
                    fields.add(betweenField);
                    break;
                case "in":
                    List<String> listValues = (List) filter.getValue();
                    InField inField = new InField();
                    inField.setDimension(filter.getDimension());
                    inField.setValues(listValues);
                    fields.add(inField);
                    break;
                case "not in":
                    List<String> listValuesNotIn = (List) filter.getValue();
                    NotInField notInField = new NotInField();
                    notInField.getField().setDimension(filter.getDimension());
                    notInField.getField().setValues(listValuesNotIn);
                    fields.add(notInField);
                    break;
            }
        }

        return fields;
    }

    private static class Query {
        String queryType = "lucene_select";
        String dataSource;
        String intervals = "1000/3000";
        String granularity = "all";
        Context context = new Context();
        Filter filter = new Filter();
        List<Dimension> dimensions = new ArrayList<>();
        PagingSpec pagingSpec = new PagingSpec();

        public Query() {
            for (int i = 0; i < 3; i++) {
                Dimension dimension = new Dimension();
                dimensions.add(dimension);
            }
        }

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

        public String getIntervals() {
            return intervals;
        }

        public void setIntervals(String intervals) {
            this.intervals = intervals;
        }

        public String getGranularity() {
            return granularity;
        }

        public void setGranularity(String granularity) {
            this.granularity = granularity;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public List<Dimension> getDimensions() {
            return dimensions;
        }

        public void setDimensions(List<Dimension> dimensions) {
            this.dimensions = dimensions;
        }

        public PagingSpec getPagingSpec() {
            return pagingSpec;
        }

        public void setPagingSpec(PagingSpec pagingSpec) {
            this.pagingSpec = pagingSpec;
        }
    }

    private static class Context {
        int timeout = 180000;
        boolean useOffheap = true;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public boolean isUseOffheap() {
            return useOffheap;
        }

        public void setUseOffheap(boolean useOffheap) {
            this.useOffheap = useOffheap;
        }
    }

    private static class FilterDimension {
        @JsonProperty
        String dimension;
        @JsonProperty
        String action;
        @JsonProperty
        Object value;

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private static class Filter {
        String type = "and";
        List<FieldType> fields = new ArrayList<>();

        public Filter() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<FieldType> getFields() {
            return fields;
        }

        public void setFields(List<FieldType> fields) {
            this.fields = fields;
        }
    }

    private static class Field extends FieldType {
        String dimension;

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }
    }

    private static class FieldType {
        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private static class EqualField extends Field {
        public EqualField() {
            super.type = "selector";
        }

        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class NotEqualField extends FieldType {
        public NotEqualField() {
            super.type = "not";
            field = new EqualField();
        }

        EqualField field;

        public EqualField getField() {
            return field;
        }

        public void setField(EqualField field) {
            this.field = field;
        }
    }

    private static class InField extends Field {
        List<String> values = new ArrayList<>();

        public InField() {
            super.type = "in";
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    private static class NotInField extends FieldType {
        public NotInField() {
            super.type = "not";
            field = new InField();
        }

        InField field;

        public InField getField() {
            return field;
        }

        public void setField(InField field) {
            this.field = field;
        }
    }

    private static class GreaterThanEqualField extends BoundField {
        String lower;

        public String getLower() {
            return lower;
        }

        public void setLower(String lower) {
            this.lower = lower;
        }
    }

    private static class GreaterThanField extends GreaterThanEqualField {
        Boolean lowerStrict = true;

        public Boolean getLowerStrict() {
            return lowerStrict;
        }

        public void setLowerStrict(Boolean lowerStrict) {
            this.lowerStrict = lowerStrict;
        }
    }

    private static class LessThanEqualField extends BoundField {
        String upper;

        public String getUpper() {
            return upper;
        }

        public void setUpper(String upper) {
            this.upper = upper;
        }
    }

    private static class LessThanField extends LessThanEqualField {
        Boolean upperStrict = true;

        public Boolean getUpperStrict() {
            return upperStrict;
        }

        public void setUpperStrict(Boolean upperStrict) {
            this.upperStrict = upperStrict;
        }
    }

    private static class BetweenField extends BoundField {
        String lower;
        String upper;

        public String getLower() {
            return lower;
        }

        public void setLower(String lower) {
            this.lower = lower;
        }

        public String getUpper() {
            return upper;
        }

        public void setUpper(String upper) {
            this.upper = upper;
        }
    }

    private static class BetweenEqualField extends BetweenField {
        Boolean lowerStrict = true;
        Boolean upperStrict = true;

        public Boolean getLowerStrict() {
            return lowerStrict;
        }

        public void setLowerStrict(Boolean lowerStrict) {
            this.lowerStrict = lowerStrict;
        }

        public Boolean getUpperStrict() {
            return upperStrict;
        }

        public void setUpperStrict(Boolean upperStrict) {
            this.upperStrict = upperStrict;
        }
    }

    private static class BoundField extends Field {
        public BoundField() {
            super.type = "bound";
        }
    }

    private static class Dimension {
        String type = "default";
        String dimension;
        String outputName;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public String getOutputName() {
            return outputName;
        }

        public void setOutputName(String outputName) {
            this.outputName = outputName;
        }
    }

    private static class PagingSpec {
        Map<String, Integer> pagingIdentifiers = Maps.newHashMap();
        Integer threshold = 5000;

        public Map<String, Integer> getPagingIdentifiers() {
            return pagingIdentifiers;
        }

        public void setPagingIdentifiers(Map<String, Integer> pagingIdentifiers) {
            this.pagingIdentifiers = pagingIdentifiers;
        }

        public Integer getThreshold() {
            return threshold;
        }

        public void setThreshold(Integer threshold) {
            this.threshold = threshold;
        }
    }

    public static void main(String[] args) {
        PathAnalysisDto dto = new PathAnalysisDto();
        dto.setDataSource("com_SJLnjowGe_project_H1sSFD36g");
        dto.setStartDate("2017-01-01");
        dto.setEndDate("2017-05-05");
        dto.setHomePage("蠢蠢欲动");
        dto.setPages(Lists.asList("蠢蠢欲动", "激情无限", new String[]{"欲罢不能", "爷不行了"}));

        ColumnName columnName = new ColumnName();
        columnName.setSessionId("SugoSessionId");
        columnName.setPageName("Page");
        columnName.setDate("AccessTime");
        columnName.setUserId("SugoUserId");
        dto.setDimension(columnName);

        List<FilterDimension> filters = new ArrayList<>();
        FilterDimension filterDimension = new FilterDimension();
        filterDimension.setDimension("sugo_id");
        filterDimension.setAction("=");
        filterDimension.setValue("1001");
        filters.add(filterDimension);

        filterDimension = new FilterDimension();
        filterDimension.setDimension("age");
        filterDimension.setAction(">");
        filterDimension.setValue("18");
        filters.add(filterDimension);

        dto.setFilters(filters);

        try {
            System.out.println(jsonMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /*long before = System.currentTimeMillis();
        String path = "蠢蠢欲动,激情无限,欲罢不能,爷不行了|";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000000; i++) {
            sb.append(path);
        }
        byte[] bytes = sb.toString().getBytes();
        long after = System.currentTimeMillis();
        System.out.println("生成路径耗时：" + (after - before) + "ms");

        String paths = new String(bytes);
        String[] pathList = paths.split("\\|");
        System.out.println("路径个数：" + pathList.length);
        long now = System.currentTimeMillis();
        System.out.println("反序列化路径耗时：" + (now - after) + "ms");*/
    }

}
