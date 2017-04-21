package io.sugo.pio.scripting;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;

/*
 */
public interface ScriptRunner {
    List<Class<? extends IOObject>> getSupportedTypes();

    List<IOObject> run(List<IOObject> inputs, int numberOfOutputPorts) throws IOException, CancellationException, OperatorException;

    void cancel();

    public abstract void registerLogger(Logger paramLogger);
}
