package sugo.io.pio.engine.template.data;

import org.apache.spark.sql.Dataset;

/**
 */
public class TemplateTrainingData {
    private final Dataset data;
    private final Dataset testdata;

    public TemplateTrainingData(Dataset data, Dataset tdata) {
        this.data = data;
        this.testdata = tdata;
    }

    public Dataset getData() {
        return data;
    }
    public Dataset getTestdata(){
        return testdata;
    }
}
