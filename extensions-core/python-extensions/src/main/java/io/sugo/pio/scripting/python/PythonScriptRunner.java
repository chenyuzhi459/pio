package io.sugo.pio.scripting.python;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessStoppedException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.scripting.AbstractScriptRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 */
public class PythonScriptRunner extends AbstractScriptRunner {
    public PythonScriptRunner(String script, Operator operator) {
        super(addEncoding(script), operator);
    }

    @Override
    protected void serialize(IOObject var1, File var2) throws FileNotFoundException, IOException, UserError, ProcessStoppedException {

    }

    @Override
    protected IOObject deserialize(File var1) throws IOException, UserError {
        return null;
    }

    private static String addEncoding(String script) {
        return "# coding=utf-8\n" + script;
    }

    @Override
    public List<Class<? extends IOObject>> getSupportedTypes() {
        return null;
    }


    @Override
    public void cancel() {
    }

    @Override
    protected String getFileExtension(IOObject object) {
        if(object instanceof ExampleSet) {
            return "csv";
        } else if(object instanceof PythonNativeObject) {
            return "bin";
        } else if(object instanceof FileObject) {
            return "foi";
        } else {
            throw new IllegalArgumentException("object type not supported");
        }
    }
}
