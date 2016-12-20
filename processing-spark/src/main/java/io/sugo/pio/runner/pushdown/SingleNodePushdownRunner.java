package io.sugo.pio.runner.pushdown;

import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class SingleNodePushdownRunner extends PushdownRunner {
    public static void main(String[] encodedArgs)
            throws Throwable {
        new SingleNodePushdownRunner().run(encodedArgs);
    }

    @Override
    protected OutputParams runJob(JavaSparkContext sparkContext, InputParams inputParams) throws SparkException {
        return new OutputParams();
    }
}
