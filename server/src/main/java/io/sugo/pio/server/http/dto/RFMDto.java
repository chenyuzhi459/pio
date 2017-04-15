package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class RFMDto {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private String datasource;

    private ColumnName scene;

    /**
     * yyyy-mm-dd
     */
    private String startDate;

    /**
     * yyyy-mm-dd
     */
    private String endDate;

    private static class ColumnName {
        @JsonProperty("UserID")
        String userId;
        @JsonProperty("Price")
        String price;
        @JsonProperty("Date")
        String date;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public ColumnName getScene() {
        return scene;
    }

    public void setScene(ColumnName scene) {
        this.scene = scene;
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

    public String getQuery() {
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
    }

    public String buildQuery() {
        Query query = new Query();
        query.setDataSource(this.datasource);
//        query.setIntervals(this.startDate + "/" + this.endDate);
        query.getFilter().setDimension(this.scene.getDate());
        query.getFilter().setLower(this.startDate);
        query.getFilter().setUpper(this.endDate);
        query.getDimensions().get(0).setDimension(this.scene.getUserId());
        query.getAggregations().get(0).setFieldName(this.scene.getDate());
        query.getAggregations().get(2).setFieldName(this.scene.getPrice());

        String queryStr = "";
        try {
            queryStr = jsonMapper.writeValueAsString(query);
        } catch (JsonProcessingException ignore) { }

        return queryStr;
    }

    private static class Query {
        String queryType = "lucene_groupBy";
        String dataSource;
        String intervals = "1000/3000";
        String granularity = "all";
        Context context = new Context();
        Filter filter = new Filter();
        List<Dimension> dimensions = new ArrayList<>();
        List<Aggregation> aggregations = new ArrayList<>();
        LimitSpec limitSpec = new LimitSpec();

        public Query() {
            Dimension dimension = new Dimension();
            dimensions.add(dimension);

            Aggregation recency = new Aggregation();
            recency.setName("lastTime");
            recency.setType("lucene_dateMax");
            aggregations.add(recency);

            Aggregation frequency = new Aggregation();
            frequency.setName("frequency");
            frequency.setType("lucene_count");
            aggregations.add(frequency);

            Aggregation monetary = new Aggregation();
            monetary.setName("monetary");
            monetary.setType("lucene_doubleSum");
            aggregations.add(monetary);
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

        public List<Aggregation> getAggregations() {
            return aggregations;
        }

        public void setAggregations(List<Aggregation> aggregations) {
            this.aggregations = aggregations;
        }

        public LimitSpec getLimitSpec() {
            return limitSpec;
        }

        public void setLimitSpec(LimitSpec limitSpec) {
            this.limitSpec = limitSpec;
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
        String type = "bound";
        String dimension;
        String lower;
        String upper;

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

    private static class Dimension {
        String type = "default";
        String dimension;
        String outputName = "userId";

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

    private static class Aggregation {
        String name;
        String type;
        String fieldName = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }

    private static class LimitSpec {
        String type = "default";
        List<Column> columns = new ArrayList<>();

        public LimitSpec() {
            Column column = new Column();
            columns.add(column);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }
    }

    private static class Column {
        String dimension = "userId";

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }
    }

}
