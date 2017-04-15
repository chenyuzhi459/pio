package io.sugo.pio.scripting.python;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessStoppedException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.scripting.AbstractScriptRunner;
import io.sugo.pio.tools.ParameterService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        ArrayList types = new ArrayList();
        types.add(ExampleSet.class);
        types.add(PythonNativeObject.class);
        types.add(FileObject.class);
        return types;
    }

    protected String getUserscriptFilename() {
        return "userscript.py";
    }

    public Process start(Path directory, int numberOfOutputPorts) throws IOException {
        Path wrapperPath = Paths.get(directory.toString(), new String[]{"wrapper.py"});
        Files.copy(PythonScriptRunner.class.getResourceAsStream("/python/wrapper.py"), wrapperPath, new CopyOption[0]);
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"python", "-u", "wrapper.py", "" + numberOfOutputPorts});
        processBuilder.directory(directory.toFile());
        Map env = processBuilder.environment();
        env.put("PYTHONIOENCODING", StandardCharsets.UTF_8.name());
        return getProcessWithLogging(processBuilder);
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

    protected void handleLanguageSpecificExitCode(int exitCode, String errorString) throws UserError {
        PythonExitCode code = PythonExitCode.getForCode(exitCode);
        if(code != null) {
            throw new UserError(getOperator(), code.getUserErrorKey(), new Object[]{errorString});
        }
    }
}
