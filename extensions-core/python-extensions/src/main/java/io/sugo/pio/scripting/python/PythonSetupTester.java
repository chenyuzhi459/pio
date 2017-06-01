package io.sugo.pio.scripting.python;

import io.sugo.pio.scripting.AbstractSetupTester;
import io.sugo.pio.tools.ParameterService;
import io.sugo.pio.tools.SystemInfoUtilities;

import java.nio.file.Path;

/**
 */
public final class PythonSetupTester extends AbstractSetupTester {
    public static final String PANDAS_MINUMUM_VERSION = "0.12.0";
    private static final int MODULE_TEST_FAILED = 77;
    private static final String PYTHON_FILE_EXTENSION = ".py";
    private static final String C_PICKLE_MODULE_NAME = "cPickle";
    private static final String PANDAS_MODULE_NAME = "pandas";
    private static final String WINDOWS_DEFAULT_PYTHON_PATH = "C:/Python/python.exe";
    private static final String VERSION = "--version";
    private static final String IMPORT = "import ";
    private static final String IMPORT_TEST = "import sys%ntry:%n    import  %s%nexcept:%n    sys.exit(%d)";
    private static final String PYTHON = "python";
    private static final String[] LINUX_MAC_FOLDERS = {"/usr/bin", "/usr/local/bin"};
    private static final String LINUX_MAC_PYTHON_GLOB = "python*";
    private static final String PYTHON_EXE = "python.exe";
    private static final String[] WINDOWS_FOLDER_GLOBS = {"Anaconda*", "Python*"};
    private static final String[] WINDOWS_PATH_PREFIXES = {"C:/", "C:/Program Files"};
    public static final PythonSetupTester INSTANCE = new PythonSetupTester();
    private static final String PYTHON2_IMPORT = "import sys\nif sys.version_info < (3, 0):\n    import ";
    private static final String PYTHON2_IMPORT_TEST = "import sys%nif sys.version_info < (3, 0):%n    try:%n        import %s%n    except:%n        sys.exit(%d)";
    private static final String PANDAS_VERSION_TEST = "import pandas%nimport sys%nif float(pandas.__version__.split('.')[1])<%d:%n    sys.exit(%d)";
    private static final int PANDAS_MINIMAL_MAJOR_VERSION = 12;
    private boolean pythonFound = false;
    private boolean pandasFound = false;
    private boolean cpickleFound = false;
    private boolean pandasVersionSufficient = false;

    private PythonSetupTester() {
    }

    public void resetCache() {
        this.pythonFound = false;
        this.pandasFound = false;
        this.cpickleFound = false;
    }

    public boolean wasPythonFound() {
        if (!this.pythonFound) {
            this.pythonFound = scriptingPathTest();
        }
        return this.pythonFound;
    }

    public boolean wasPandasFound() {
        if (!this.pandasFound) {
            this.pandasFound = isModuleInstalled("pandas", false);
        }
        return this.pandasFound;
    }

    public boolean wasCpickleFound() {
        if (!this.cpickleFound) {
            this.cpickleFound = isModuleInstalled("cPickle", true);
        }
        return this.cpickleFound;
    }

    public boolean wasPandasVersionSufficient() {
        if (!this.pandasVersionSufficient) {
            this.pandasVersionSufficient = checkPandasVersion();
        }
        return this.pandasVersionSufficient;
    }

    public boolean isPythonInstalled() {
        resetCache();
        this.pythonFound = scriptingPathTest();
        return this.pythonFound;
    }

    private boolean scriptingPathTest() {
        String pythonPath = ParameterService.getParameterValue("rapidminer.python_scripting.path");
        if (!pythonPath.contains("python")) {
            return false;
        }
        if (SystemInfoUtilities.getOperatingSystem() == SystemInfoUtilities.OperatingSystem.WINDOWS) {
            if ((!pythonPath.startsWith("\"")) && (!pythonPath.endsWith("\""))) {
                pythonPath = "\"" + pythonPath + "\"";
            }
        }
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{pythonPath, "--version"});
        return processTestFast(processBuilder);
    }

    public void autodetectPath() {
        Path pythonPath = checkOnPath("python.exe", "python", "Anaconda");
        if (pythonPath == null) {
            if (SystemInfoUtilities.getOperatingSystem() == SystemInfoUtilities.OperatingSystem.WINDOWS) {
                pythonPath = checkPossibleWindowsFolders(WINDOWS_PATH_PREFIXES, WINDOWS_FOLDER_GLOBS, "python.exe");
            } else {
                pythonPath = checkPossibleLinuxMacFolders(LINUX_MAC_FOLDERS, "python*");
            }
        }
        String pathString;
        if (pythonPath != null) {
            pathString = pythonPath.toAbsolutePath().toString();
        } else {
            if (SystemInfoUtilities.getOperatingSystem() == SystemInfoUtilities.OperatingSystem.WINDOWS) {
                pathString = "C:/Python/python.exe";
            } else {
                pathString = "python";
            }
        }
        ParameterService.setParameterValue("rapidminer.python_scripting.path", pathString);
    }

    private boolean isModuleInstalled(String moduleName, boolean onlyPython2) {
        String pythonPath = ParameterService.getParameterValue("rapidminer.python_scripting.path");
        String test;
        if (onlyPython2) {
            test = "import sys\nif sys.version_info < (3, 0):\n    import " + moduleName;
        } else {
            test = "import " + moduleName;
        }
        return checkScriptForSuccess(test, pythonPath, ".py");
    }

    private boolean checkPandasVersion() {
        String pythonPath = ParameterService.getParameterValue("rapidminer.python_scripting.path");
        String script = String.format("import pandas%nimport sys%nif float(pandas.__version__.split('.')[1])<%d:%n    sys.exit(%d)", new Object[]{Integer.valueOf(12), Integer.valueOf(77)});
        return checkScriptForSuccess(script, pythonPath, ".py");
    }

    private boolean moduleNotFound(String moduleName, String pythonPath, boolean onlyPython2) {
        String test;
        if (onlyPython2) {
            test = String.format("import sys%nif sys.version_info < (3, 0):%n    try:%n        import %s%n    except:%n        sys.exit(%d)", new Object[]{moduleName, Integer.valueOf(77)});
        } else {
            test = String.format("import sys%ntry:%n    import  %s%nexcept:%n    sys.exit(%d)", new Object[]{moduleName, Integer.valueOf(77)});
        }
        return checkScriptForExitCode(test, pythonPath, 77, ".py");
    }

    private boolean pandasVersionNotSufficient(String pythonPath) {
        String script = String.format("import pandas%nimport sys%nif float(pandas.__version__.split('.')[1])<%d:%n    sys.exit(%d)", new Object[]{Integer.valueOf(12), Integer.valueOf(77)});
        return checkScriptForExitCode(script, pythonPath, 77, ".py");
    }
}
