package io.sugo.pio.scripting;

import io.sugo.pio.operator.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 */
public abstract class AbstractScriptRunner implements ScriptRunner {

    private final String script;
    private Process process;
    private final Operator operator;
    private Logger logger;

    public AbstractScriptRunner(String script, Operator operator) {
        this.script = script;
        this.operator = operator;
    }

    protected Operator getOperator() {
        return operator;
    }

    protected abstract void serialize(IOObject var1, File var2) throws FileNotFoundException, IOException, UserError, ProcessStoppedException;

    protected abstract IOObject deserialize(File var1) throws IOException, UserError;

    protected abstract Process start(Path tempFolder, int numberOfOutputPorts) throws IOException;

    public void registerLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<IOObject> run(List<IOObject> inputs, int numberOfOutputPorts) throws IOException, CancellationException, OperatorException {
        Path tempFolder = null;

        List<IOObject> result;
        try {
            tempFolder = Files.createTempDirectory("scripting", new FileAttribute[0]);
            serializeInputs(inputs, tempFolder);
            generateScriptFile(tempFolder);
            process = start(tempFolder, numberOfOutputPorts);
//
            try {
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    String errorString = getError(tempFolder);
                    logger.log(Level.SEVERE, "Execute python script failed: " + errorString);

                    handleLanguageSpecificExitCode(exitCode, errorString);
                    if (errorString.isEmpty()) {
                        throw new OperatorException(OperatorException.getErrorMessage("python_scripting.script_failed", new Object[0]));
                    }

                    throw new OperatorException(OperatorException.getErrorMessage("python_scripting.script_failed_message", new Object[]{errorString}));
                }
            } catch (InterruptedException var9) {
                cancel();
                new CancellationException();
            }

            result = deserializeResults(tempFolder);
        } finally {
            deleteTempFolder(tempFolder);
        }
//
        return result;
    }

    protected String getError(Path tempFolder) {
        try {
            byte[] e = Files.readAllBytes(Paths.get(tempFolder.toString(), new String[]{"rapidminer_error.log"}));
            return new String(e, StandardCharsets.UTF_8);
        } catch (IOException var3) {
            return "";
        }
    }

    protected abstract void handleLanguageSpecificExitCode(int errorCode, String errorString) throws UserError;

    public void cancel() {
        if (process != null) {
            process.destroy();

            try {
                process.waitFor();
            } catch (InterruptedException ie) {
            }
        }
    }

    private List<File> serializeInputs(List<IOObject> inputs, Path tempFolder) throws IOException, UserError, ProcessStoppedException {
        ArrayList<File> inputFiles = new ArrayList(inputs.size());
        int index = 0;

        for (Iterator<IOObject> iterator = inputs.iterator(); iterator.hasNext(); ++index) {
            IOObject input = iterator.next();
            Path tempPath = Paths.get(tempFolder.toString(), new String[]{"pio_input" + String.format("%03d", new Object[]{Integer.valueOf(index)}) + "." + getFileExtension(input)});
            File tempFile = tempPath.toFile();
            serialize(input, tempFile);
            inputFiles.add(tempFile);
        }

        return inputFiles;
    }

    private List<IOObject> deserializeResults(Path tempFolder) throws IOException, UserError {
        LinkedList<Path> outputFiles = new LinkedList();
        Pattern pattern = Pattern.compile("pio_output[0-9]{3}\\..*");
        DirectoryStream comparator = Files.newDirectoryStream(tempFolder);
        Throwable outputs = null;
        Path outputFile;
        try {
            Iterator<Path> iterator = comparator.iterator();
            while (iterator.hasNext()) {
                outputFile = iterator.next();
                if (pattern.matcher(outputFile.getFileName().toString()).matches()) {
                    outputFiles.add(outputFile);
                }
            }
        } catch (Throwable t) {
            outputs = t;
            throw t;
        } finally {
            if (comparator != null) {
                if (outputs != null) {
                    try {
                        comparator.close();
                    } catch (Throwable t) {
                        outputs.addSuppressed(t);
                    }
                } else {
                    comparator.close();
                }
            }
        }

        Comparator<Path> comparator1 = new Comparator<Path>() {
            public int compare(Path o1, Path o2) {
                String name1 = o1.getFileName().toString();
                String name2 = o2.getFileName().toString();
                return name1.compareTo(name2);
            }
        };
        Collections.sort(outputFiles, comparator1);
        LinkedList<IOObject> result = new LinkedList();
        Iterator<Path> iterator = outputFiles.iterator();

        while (iterator.hasNext()) {
            outputFile = iterator.next();
            IOObject object = deserialize(outputFile.toFile());
            if (object != null) {
                result.add(object);
            }
        }

        return result;
    }

    protected abstract String getFileExtension(IOObject input);

    private void deleteTempFolder(Path tempFolder) {
        if (tempFolder != null) {
            try {
                DirectoryStream directoryStream = Files.newDirectoryStream(tempFolder);
                Throwable outputs = null;

                try {
                    Iterator iterator = directoryStream.iterator();
                    label132:
                    while (true) {
                        while (true) {
                            if (!iterator.hasNext()) {
                                break label132;
                            }

                            Path entry = (Path) iterator.next();
                            if (Files.isDirectory(entry, new LinkOption[0])) {
                                deleteTempFolder(entry);
                            } else {
                                try {
                                    Files.delete(entry);
                                } catch (IOException | SecurityException e) {
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    outputs = t;
                    throw t;
                } finally {
                    if (directoryStream != null) {
                        if (outputs != null) {
                            try {
                                directoryStream.close();
                            } catch (Throwable t) {
                                outputs.addSuppressed(t);
                            }
                        } else {
                            directoryStream.close();
                        }
                    }
                }
            } catch (IOException ioe) {
            }

            try {
                Files.delete(tempFolder);
            } catch (IOException | SecurityException e) {
            }
        }
    }

    private File generateScriptFile(Path tempFolder) throws IOException {
        Path tempPath = Paths.get(tempFolder.toString(), new String[]{getUserscriptFilename()});
        Files.write(tempPath, script.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        return tempPath.toFile();
    }

    protected Process getProcessWithLogging(ProcessBuilder processBuilder) throws IOException {
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);
        Process process = processBuilder.start();
        InputStreamLogger.log(process.getInputStream(), logger);
        return process;
    }

    protected abstract String getUserscriptFilename();
}
