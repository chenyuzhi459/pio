package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;
import sugo.io.pio.engine.DataSource;
import sugo.io.pio.engine.template.data.TemplateTrainingData;

/**
 */
public class TemplateDatasource implements DataSource<TemplateTrainingData> {
    @Override
    public TemplateTrainingData readTraining(JavaSparkContext sc, BatchEventHose eventHose) {
        JavaRDD<Event> events = eventHose.find(sc);
        SQLContext sqlContext = new SQLContext(sc);

        Dataset ratingEvents =sqlContext.createDataFrame(events, Event.class);
        Dataset[] splits = ratingEvents.randomSplit(new double[]{0.8, 0.2});
        Dataset training = splits[0];
        Dataset test = splits[1];

        return new TemplateTrainingData(training, test);
    }
}
