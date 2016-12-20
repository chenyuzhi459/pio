package io.sugo.pio.runner.pushdown;

import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class MultiNodePushdownRunner extends PushdownRunner {
    public static void main(String[] encodedArgs)
            throws Throwable {
        new MultiNodePushdownRunner().run(encodedArgs);
    }

    @Override
    protected OutputParams runJob(JavaSparkContext sparkContext, InputParams inputParams) throws SparkException {
        return new OutputParams();
    }
}
