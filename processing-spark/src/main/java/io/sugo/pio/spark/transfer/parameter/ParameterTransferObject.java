package io.sugo.pio.spark.transfer.parameter;

/**
 */
public class ParameterTransferObject {
    public interface ParameterKey {
        Class<?> getType();
    }
}
