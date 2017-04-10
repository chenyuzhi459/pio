package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class RFMDto {

    private String datasource;

    private ColumnName scene;

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

    public String getQuery() {
        StringBuffer sb = new StringBuffer("select ")
                .append(this.getScene().getUserId()).append(" as userId, ")
                .append("max(").append(this.getScene().getDate()).append(") as lastTime, ")
                .append("count(1) as frequency, ")
                .append("sum(").append(this.getScene().getPrice()).append(") as monetary ")
                .append("from ").append(this.getDatasource())
                .append(" group by ").append(this.getScene().getUserId());

        return sb.toString();
    }
}
