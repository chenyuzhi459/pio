package io.sugo.pio.engine.template.data;

import org.apache.spark.sql.Dataset;

/**
 */
public class TemplatePreparedData {
    private final Dataset data;

    public TemplatePreparedData(Dataset data) {
        this.data = data;
    }

    public Dataset getData() {
        return data;
    }

}
