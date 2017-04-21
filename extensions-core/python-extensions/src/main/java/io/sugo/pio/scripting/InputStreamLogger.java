package io.sugo.pio.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class InputStreamLogger {

    public static void log(InputStream stream, final Logger logger) {
        Runnable outputReader = new Runnable() {
            public void run() {
                String line;
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    Throwable localThrowable3 = null;
                    try {
                        while ((line = bufferedReader.readLine()) != null) {
                            logger.log(Level.INFO, line);
                        }
                    } catch (Throwable localThrowable1) {
                        localThrowable3 = localThrowable1;
                        throw localThrowable1;
                    } finally {
                        if (bufferedReader != null) {
                            if (localThrowable3 != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable localThrowable2) {
                                    localThrowable3.addSuppressed(localThrowable2);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }
                    }
                } catch (IOException localIOException) {
                }
            }
        };

        Thread outputReaderThread = new Thread(outputReader);
        outputReaderThread.setDaemon(true);
        outputReaderThread.start();
    }
}
