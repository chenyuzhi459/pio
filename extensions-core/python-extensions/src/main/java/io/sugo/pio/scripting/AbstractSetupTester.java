package io.sugo.pio.scripting;

import java.io.IOException;

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
}
