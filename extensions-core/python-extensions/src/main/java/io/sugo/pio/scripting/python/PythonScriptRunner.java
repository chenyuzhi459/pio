package io.sugo.pio.scripting.python;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeRole;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.io.CSVExampleSetWriter;
import io.sugo.pio.operator.nio.file.BufferedFileObject;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.operator.nio.file.SimpleFileObject;
import io.sugo.pio.scripting.AbstractScriptRunner;
import io.sugo.pio.scripting.ScriptingCSVExampleSource;
import io.sugo.pio.tools.Ontology;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
public class PythonScriptRunner extends AbstractScriptRunner {

    private static final Logger logger = new Logger(PythonScriptRunner.class);

    public PythonScriptRunner(String script, Operator operator) {
        super(addEncoding(script), operator);
    }

    @Override
    protected void serialize(IOObject object, File file) throws FileNotFoundException, IOException, UserError, ProcessStoppedException {
        Throwable localThrowable18;
        if ((object instanceof ExampleSet)) {
            ExampleSet exampleSet = (ExampleSet) object;
            OutputStream outputStream = new FileOutputStream(file);
            localThrowable18 = null;
            try {
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                Throwable localThrowable19 = null;
                try {
                    CSVExampleSetWriter.writeCSV(exampleSet, printer, ",", true, true, true, "inf", getOperator().getProgress());
                    printer.flush();
                } catch (Throwable localThrowable1) {
                    throw localThrowable1;
                } finally {
                }
            } catch (Throwable localThrowable4) {
                localThrowable18 = localThrowable4;
                throw localThrowable4;
            } finally {
                if (outputStream != null) {
                    if (localThrowable18 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable localThrowable5) {
                            localThrowable18.addSuppressed(localThrowable5);
                        }
                    } else {
                        outputStream.close();
                    }
                }
            }
            writeMetaData(exampleSet, file);
        } else if ((object instanceof PythonNativeObject)) {
            PythonNativeObject pythonObject = (PythonNativeObject) object;
            ByteArrayInputStream stream = pythonObject.openStream();
            localThrowable18 = null;
            try {
                Files.copy(stream, file.toPath(), new CopyOption[0]);
            } catch (Throwable localThrowable7) {
                localThrowable18 = localThrowable7;
                throw localThrowable7;
            } finally {
                if (stream != null) {
                    if (localThrowable18 != null) {
                        try {
                            stream.close();
                        } catch (Throwable localThrowable8) {
                            localThrowable18.addSuppressed(localThrowable8);
                        }
                    } else {
                        stream.close();
                    }
                }
            }
        } else if ((object instanceof FileObject)) {
            String path = "";
            Object stream;
            if ((object instanceof BufferedFileObject)) {
                BufferedFileObject bufferedFile = (BufferedFileObject) object;
                Path workingDirectoryPython = Paths.get(file.getParent(), new String[0]);
                Path tempFile = Files.createTempFile(workingDirectoryPython, "rm_file_", ".dump", new FileAttribute[0]);

                stream = bufferedFile.openStream();
                Throwable localThrowable1 = null;
                try {
                    Files.copy((InputStream) stream, tempFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                } catch (Throwable localThrowable21) {
                    localThrowable1 = localThrowable21;
                    throw localThrowable21;
                } finally {
                    if (stream != null) {
                        if (localThrowable1 != null) {
                            try {
                                ((ByteArrayInputStream) stream).close();
                            } catch (Throwable localThrowable11) {
                                localThrowable1.addSuppressed(localThrowable11);
                            }
                        } else {
                            ((ByteArrayInputStream) stream).close();
                        }
                    }
                }
                path = tempFile.toString();
            } else {
                FileObject foiFile = (FileObject) object;
                try {
                    path = foiFile.getFile().getAbsolutePath();
                } catch (OperatorException e) {
                    throw new UserError(getOperator(), "python_scripting.serialization", new Object[]{((OperatorException) e).getMessage()});
                }
            }
            OutputStream outputStream = new FileOutputStream(file);
            Throwable e = null;
            try {
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                stream = null;
                try {
                    printer.print(path);
                    printer.flush();
                } catch (Throwable localThrowable13) {
                    stream = localThrowable13;
                    throw localThrowable13;
                } finally {
                }
            } catch (Throwable localThrowable16) {
                e = localThrowable16;
                throw localThrowable16;
            } finally {
                if (outputStream != null) {
                    if (e != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable localThrowable17) {
                            ((Throwable) e).addSuppressed(localThrowable17);
                        }
                    } else {
                        outputStream.close();
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("object type not supported");
        }
    }

    private void writeMetaData(ExampleSet exampleSet, File exampleSetFile) {
        String parent = exampleSetFile.getParent();
        String name = exampleSetFile.getName();

        String newName = name.replace(".csv", ".pmd");
        Path metaDataPath = Paths.get(parent, new String[]{newName});
        try {
            OutputStream outputStream = new FileOutputStream(metaDataPath.toFile());
            Throwable localThrowable6 = null;
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                try {
                    out.print("{");
                    Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
                    boolean first = true;
                    while (a.hasNext()) {
                        if (!first) {
                            out.print(", ");
                        } else {
                            first = false;
                        }
                        Attribute attribute = (Attribute) a.next();
                        String attributeName = attribute.getName();
                        attributeName = attributeName.replaceAll("\"", "'");
                        out.print("\"" + attributeName + "\"");

                        out.print(": [");

                        out.print("\"" + Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(attribute.getValueType()) + "\"");

                        out.print(", ");

                        AttributeRole role = exampleSet.getAttributes().findRoleByName(attribute.getName());
                        String roleName;
                        if (role == null) {
                            roleName = "attribute";
                        } else {
                            roleName = role.getSpecialName() == null ? "attribute" : role.getSpecialName();
                            roleName = roleName.replaceAll("\"", "'");
                        }
                        out.print("\"" + roleName + "\"");

                        out.print("]");
                    }
                    out.print("}");
                    out.flush();
                } catch (Throwable localThrowable1) {
                    throw localThrowable1;
                } finally {
                }
            } catch (Throwable localThrowable4) {
                localThrowable6 = localThrowable4;
                throw localThrowable4;
            } finally {
                if (outputStream != null) {
                    if (localThrowable6 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable localThrowable5) {
                            localThrowable6.addSuppressed(localThrowable5);
                        }
                    } else {
                        outputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to send meta data to Python.");
        }
    }

    @Override
    protected IOObject deserialize(File file) throws IOException, UserError {
        String fileName = file.getName();
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        if ("csv".equals(extension)) {
            try {
                ScriptingCSVExampleSource csvSource = new ScriptingCSVExampleSource();
                csvSource.setParameter("column_separators", ",");
                csvSource.setParameter("csv_file", file.getAbsolutePath());
                csvSource.setParameter("encoding", StandardCharsets.UTF_8.name());
                csvSource.setParameter("date_format", "yyyy-MM-dd HH:mm:ss");
                csvSource.setNumberFormat(new PythonDecimalFormat());

                String parent = file.getParent();
                String name = file.getName();
                String newName = name.replace(".csv", ".pmd");
                Path metaDataPath = Paths.get(parent, new String[]{newName});
                csvSource.readMetadataFromFile(metaDataPath.toFile());

                return csvSource.createExampleSet();
            } catch (OperatorException e) {
                throw new IOException("Deserialization failed", e);
            }
        }
        if ("bin".equals(extension)) {
            if (Files.size(file.toPath()) > 2147483639L) {
                throw new UserError(getOperator(), "python_scripting.deserialization.file_size");
            }
            return new PythonNativeObject(file);
        }
        if ("foi".equals(extension)) {
            String path = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            Path fileOfInterest = Paths.get(path, new String[0]);

            Path workingDirectory = file.toPath().getParent();
            if ((fileOfInterest.startsWith(workingDirectory)) || (fileOfInterest.getParent() == null)) {
                if (fileOfInterest.getParent() == null) {
                    fileOfInterest = Paths.get(workingDirectory.toString(), new String[]{fileOfInterest.toString()});
                }
                /*if (RapidMiner.getExecutionMode().isHeadless()) {
                    if (Files.size(fileOfInterest) > 2147483639L) {
                        throw new UserError(getOperator(), "python_scripting.deserialization.file_size");
                    }
                    return new BufferedFileObject(Files.readAllBytes(fileOfInterest));
                }*/
                String name = fileOfInterest.getFileName().toString();
                Path destinationFile = Files.createTempFile("scripting-studio-", "-" + name, new FileAttribute[0]);
                Files.move(fileOfInterest, destinationFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});

                destinationFile.toFile().deleteOnExit();

                return new SimpleFileObject(destinationFile.toFile());
            }
            return new SimpleFileObject(fileOfInterest.toFile());
        }
        if ("pmd".equals(extension)) {
            return null;
        }
        throw new IllegalArgumentException("File type not supported");
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
        if (object instanceof ExampleSet) {
            return "csv";
        } else if (object instanceof PythonNativeObject) {
            return "bin";
        } else if (object instanceof FileObject) {
            return "foi";
        } else {
            throw new IllegalArgumentException("object type not supported");
        }
    }

    protected void handleLanguageSpecificExitCode(int exitCode, String errorString) throws UserError {
        PythonExitCode code = PythonExitCode.getForCode(exitCode);
        if (code != null) {
            throw new UserError(getOperator(), code.getUserErrorKey(), new Object[]{errorString});
        }
    }
}
