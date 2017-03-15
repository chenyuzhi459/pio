package io.sugo.pio.scripting.python;

import io.sugo.pio.scripting.AbstractSetupTester;
import io.sugo.pio.tools.ParameterService;
import io.sugo.pio.tools.SystemInfoUtilities;
import io.sugo.pio.tools.SystemInfoUtilities.OperatingSystem;

/**
 */
public final class PythonSetupTester extends AbstractSetupTester {
    public static final PythonSetupTester INSTANCE = new PythonSetupTester();
    private boolean pythonFound = false;

    private PythonSetupTester() {
    }

    public void resetCache() {
        pythonFound = false;
    }

    public boolean isPythonInstalled() {
        resetCache();
        pythonFound = this.scriptingPathTest();
        return pythonFound;
    }

    private boolean scriptingPathTest() {
        String pythonPath = ParameterService.getParameterValue("rapidminer.python_scripting.path");
        if(!pythonPath.contains("python")) {
            return false;
        } else {
            if(SystemInfoUtilities.getOperatingSystem() == OperatingSystem.WINDOWS && !pythonPath.startsWith("\"") && !pythonPath.endsWith("\"")) {
                pythonPath = "\"" + pythonPath + "\"";
            }

            ProcessBuilder processBuilder = new ProcessBuilder(new String[]{pythonPath, "--version"});
            return this.processTestFast(processBuilder);
        }
    }
}
