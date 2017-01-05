package io.sugo.pio.overlord;

/**
 */
public interface TaskRunnerFactory<T extends TaskRunner> {
    T build();
}
