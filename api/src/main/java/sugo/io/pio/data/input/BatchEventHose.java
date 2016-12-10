package sugo.io.pio.data.input;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.rdd.RDD;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventHoseType")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = BatchEventHose.TEST, value = TestBatchEventHose.class),
})
public interface BatchEventHose {
    String TEST = "test";

    RDD<Event> find();
}
