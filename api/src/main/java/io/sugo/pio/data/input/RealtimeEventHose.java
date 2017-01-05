package io.sugo.pio.data.input;

import org.apache.spark.streaming.api.java.JavaDStream;

/**
 */
public interface RealtimeEventHose {
    JavaDStream<Event> find();
}
