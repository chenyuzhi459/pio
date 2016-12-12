package sugo.io.pio.data.input;

import org.apache.spark.streaming.dstream.DStream;

/**
 */
public interface RealtimeEventHose {
    DStream<Event> find();
}
