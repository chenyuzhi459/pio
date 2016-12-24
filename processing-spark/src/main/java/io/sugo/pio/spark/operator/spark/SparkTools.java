package io.sugo.pio.spark.operator.spark;

/**
 */
public class SparkTools {
    public static enum SparkFinalState {
        FAILED,
        SUCCEEDED,
        KILLED;

        private SparkFinalState() {
        }
    }
}
