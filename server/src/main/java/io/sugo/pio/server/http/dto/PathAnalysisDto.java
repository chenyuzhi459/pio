package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    private String dataSource;

    private ColumnName dimension;

    private List<String> pages;

    private String homePage;

    /**
     * yyyy-mm-dd
     */
    private String startDate;

    /**
     * yyyy-mm-dd
     */
    private String endDate;

    private static class ColumnName {
        String sessionId;
        String pageName;
        String date;

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

    /*public String getQuery() {
        StringBuffer sb = new StringBuffer("select ")
                .append(this.getScene().getUserId()).append(" as userId, ")
                .append("max(").append(this.getScene().getDate()).append(") as lastTime, ")
                .append("count(1) as frequency, ")
                .append("sum(").append(this.getScene().getPrice()).append(") as monetary ")
                .append(" from ").append(this.getDatasource())
                .append(" where ").append(this.getScene().getDate()).append(" >= '").append(this.getStartDate()).append("'")
                .append(" and ").append(this.getScene().getDate()).append(" <= '").append(this.getEndDate()).append("'")
                .append(" group by ").append(this.getScene().getUserId());

        return sb.toString();
    }*/

    public String buildQuery() {
        Query query = new Query();
        query.setDataSource(this.dataSource);

        InField inField = ((InField) query.getFilter().getFields().get(0));
        inField.setDimension(this.getDimension().getPageName());
        inField.setValues(this.pages);

        BoundField boundField = ((BoundField) query.getFilter().getFields().get(1));
        boundField.setDimension(this.getDimension().getDate());
        boundField.setLower(this.startDate);
        boundField.setUpper(this.endDate);

        query.getDimensions().get(0).setDimension(this.getDimension().getSessionId());
        query.getDimensions().get(0).setOutputName("sessionId");
        query.getDimensions().get(1).setDimension(this.getDimension().getPageName());
        query.getDimensions().get(1).setOutputName("pageName");
        query.getDimensions().get(2).setDimension(this.getDimension().getDate());
        query.getDimensions().get(2).setOutputName("accessTime");

        String queryStr = "";
        try {
            queryStr = jsonMapper.writeValueAsString(query);
        } catch (JsonProcessingException ignore) { }

        return queryStr;
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

    private static class Filter {
        String type = "and";
        List<Field> fields = new ArrayList<>();

        public Filter() {
            Field inField = new InField();
            Field boundField = new BoundField();
            fields.add(inField);
            fields.add(boundField);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void setFields(List<Field> fields) {
            this.fields = fields;
        }
    }

    private static class Field {
        String type;
        String dimension;

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

    private static class BoundField extends Field {
        String lower;
        Boolean lowerStrict = true;
        String upper;
        Boolean upperStrict = true;

        public BoundField() {
            super.type = "bound";
        }

        public String getLower() {
            return lower;
        }

        public void setLower(String lower) {
            this.lower = lower;
        }

        public Boolean getLowerStrict() {
            return lowerStrict;
        }

        public void setLowerStrict(Boolean lowerStrict) {
            this.lowerStrict = lowerStrict;
        }

        public String getUpper() {
            return upper;
        }

        public void setUpper(String upper) {
            this.upper = upper;
        }

        public Boolean getUpperStrict() {
            return upperStrict;
        }

        public void setUpperStrict(Boolean upperStrict) {
            this.upperStrict = upperStrict;
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
        /*PathAnalysisDto dto = new PathAnalysisDto();
        dto.setDataSource("com_SJLnjowGe_project_H1sSFD36g");
        dto.setStartDate("2017-01-01");
        dto.setEndDate("2017-05-05");
        dto.setHomePage("蠢蠢欲动");
        dto.setPages(Lists.asList("蠢蠢欲动", "激情无限",  new String[] {"欲罢不能", "爷不行了"}));

        ColumnName columnName = new ColumnName();
        columnName.setSessionId("SugoSessionId");
        columnName.setPageName("Page");
        columnName.setDate("AccessTime");
        dto.setDimesion(columnName);
        try {
            System.out.println(jsonMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        long before = System.currentTimeMillis();
        String path = "蠢蠢欲动,激情无限,欲罢不能,爷不行了|";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000000; i++) {
            sb.append(path);
        }
        byte[] bytes = sb.toString().getBytes();
        long after = System.currentTimeMillis();
        System.out.println("生成路径耗时：" + (after-before) + "ms");

        String paths = new String(bytes);
        String[] pathList = paths.split("\\|");
        System.out.println("路径个数：" + pathList.length);
        long now = System.currentTimeMillis();
        System.out.println("反序列化路径耗时：" + (now-after) + "ms");
    }

}
