package sugo.io.pio.engine.template.engine;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StringType;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;
import sugo.io.pio.engine.DataSource;
import sugo.io.pio.engine.template.data.TemplateTrainingData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class TemplateDatasource implements DataSource<TemplateTrainingData> {
    @Override
    public TemplateTrainingData readTraining(JavaSparkContext sc, BatchEventHose eventHose) {
        JavaRDD<Event> events = eventHose.find(sc);
        SQLContext sqlContext = new SQLContext(sc);

        Dataset ratingEvents =sqlContext.createDataFrame(events.map(new EventToRatingFunc()), Rating.class);
        Dataset[] splits = ratingEvents.randomSplit(new double[]{0.8, 0.2});
        Dataset training = splits[0];
        Dataset test = splits[1];

        return new TemplateTrainingData(training, test);
    }

    public static class Rating implements Serializable {
        private int userId;
        private int movieId;
        private float rating;

        public Rating(int userId, int movieId, float rating) {
            this.userId = userId;
            this.movieId = movieId;
            this.rating = rating;
        }

        public int getUserId() {
            return userId;
        }

        public int getMovieId() {
            return movieId;
        }

        public float getRating() {
            return rating;
        }
    }

    static class EventToRatingFunc implements Function<Event, Rating> {

        @Override
        public Rating call(Event e) throws Exception {
            Map<String, Object> props = e.getProperties();

            return new Rating((int)props.get("userId"), (int)props.get("movieId"), (float)props.get("rating"));
        }
    }
}
