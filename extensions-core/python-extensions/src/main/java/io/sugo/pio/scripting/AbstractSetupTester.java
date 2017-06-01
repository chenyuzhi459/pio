package io.sugo.pio.scripting;

import io.sugo.pio.tools.SystemInfoUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 */
public class AbstractSetupTester implements SetupTester {

    protected boolean processTestFast(ProcessBuilder processBuilder) {
        try {
            Process e = processBuilder.start();
            int exit = e.waitFor();
            return exit == 0;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }

    protected boolean checkScriptForSuccess(String script, String path, String fileExtension) {
        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("check", fileExtension, new FileAttribute[0]);
            Files.write(tempPath, script.getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.WRITE});
            ProcessBuilder processBuilder = new ProcessBuilder(new String[]{path, tempPath.toAbsolutePath().toString()});
            return processTestFast(processBuilder);
        } catch (IOException e) {
            boolean bool;
            return false;
        } finally {
            if (tempPath != null) {
                try {
                    Files.delete(tempPath);
                } catch (IOException localIOException3) {
                }
            }
        }
    }

    protected boolean checkScriptForExitCode(String script, String scriptingPath, int exitCode, String fileExtension) {
        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("check", fileExtension, new FileAttribute[0]);

            Files.write(tempPath, script.getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.WRITE});

            ProcessBuilder processBuilder = new ProcessBuilder(new String[]{scriptingPath, tempPath.toAbsolutePath().toString()});
            Process process = processBuilder.start();
            int exit = process.waitFor();
            return exit == exitCode;
        } catch (IOException | InterruptedException e) {
            return false;
        } finally {
            if (tempPath != null) {
                try {
                    Files.delete(tempPath);
                } catch (IOException localIOException2) {
                }
            }
        }
    }

    protected Path checkOnPath(String windowsName, String linuxMacName, String preferredFolder) {
        String pathVariable = System.getenv("PATH");
        if (pathVariable == null) {
            pathVariable = System.getenv("Path");
        }
        if (pathVariable == null) {
            return null;
        }
        String[] namesOnPath = pathVariable.split(File.pathSeparator);
        String fileName;
        if (SystemInfoUtilities.getOperatingSystem() == SystemInfoUtilities.OperatingSystem.WINDOWS) {
            fileName = windowsName;
        } else {
            fileName = linuxMacName;
        }
        if (preferredFolder != null) {
            for (String name : namesOnPath) {
                if (name.contains(preferredFolder)) {
                    Path possiblePath = Paths.get(name, new String[]{fileName});
                    if (Files.exists(possiblePath, new LinkOption[0])) {
                        return possiblePath;
                    }
                }
            }
        }
        for (String name : namesOnPath) {
            Path possiblePath = Paths.get(name, new String[]{fileName});
            if (Files.exists(possiblePath, new LinkOption[0])) {
                return possiblePath;
            }
        }
        return null;
    }

    protected Path checkPossibleWindowsFolders(String[] windowsPathPrefixes, String[] windowsFolderGlobs, String windowsFileName) {
        List<Path> dirsToCheck = new ArrayList();
        String[] arrayOfString1 = windowsPathPrefixes;
        int len = arrayOfString1.length;
        for (int i = 0; i < len; i++) {
            String dir = arrayOfString1[i];
            dirsToCheck.add(Paths.get(dir, new String[0]));
        }
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            dirsToCheck.add(Paths.get(userHome, new String[0]));
        }

        String[] arrayOfString2 = windowsFolderGlobs;
        len = arrayOfString2.length;
        String glob;
        for (int i = 0; i < len; i++) {
            glob = arrayOfString2[i];
            for (Path dir : dirsToCheck) {
                try {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, glob);
                    Throwable localThrowable4 = null;
                    try {
                        for (Path entry : stream) {
                            Path possiblePath = Paths.get(entry.toString(), new String[]{windowsFileName});
                            if (Files.exists(possiblePath, new LinkOption[0])) {
                                return possiblePath;
                            }
                        }
                    } catch (Throwable localThrowable6) {
                        localThrowable4 = localThrowable6;
                        throw localThrowable6;
                    } finally {
                        if (stream != null) {
                            if (localThrowable4 != null) {
                                try {
                                    stream.close();
                                } catch (Throwable localThrowable3) {
                                    localThrowable4.addSuppressed(localThrowable3);
                                }
                            } else {
                                stream.close();
                            }
                        }
                    }
                } catch (IOException | SecurityException localIOException) {
                }
            }
        }
        return null;
    }

    protected Path checkPossibleLinuxMacFolders(String[] linuxMacFolders, String linuxMacFileGlob) {
        List<Path> dirsToCheck = new ArrayList();
        for (String dir : linuxMacFolders) {
            dirsToCheck.add(Paths.get(dir, new String[0]));
        }
        for (Iterator<Path> it = dirsToCheck.iterator(); it.hasNext(); ) {
            Path dir = it.next();
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(dir, linuxMacFileGlob);
                Throwable localThrowable = null;
                try {
                    for (Path entry : stream) {
                        if ((Files.exists(entry, new LinkOption[0])) && (!Files.isDirectory(entry, new LinkOption[0]))) {
                            return entry;
                        }
                    }
                } catch (Throwable localThrowable5) {
                    localThrowable = localThrowable5;
                    throw localThrowable5;
                } finally {
                    if (stream != null) {
                        if (localThrowable != null) {
                            try {
                                stream.close();
                            } catch (Throwable localThrowable3) {
                                localThrowable.addSuppressed(localThrowable3);
                            }
                        } else {
                            stream.close();
                        }
                    }
                }
            } catch (IOException | SecurityException localIOException1) {
            }
        }
        return null;
    }
}
