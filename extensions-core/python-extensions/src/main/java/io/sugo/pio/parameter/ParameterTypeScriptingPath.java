package io.sugo.pio.parameter;

import io.sugo.pio.scripting.SetupTester;

/**
 */
public class ParameterTypeScriptingPath extends ParameterTypeFile {
    private static final long serialVersionUID = 5447853606628263583L;
    private final transient SetupTester tester;

    public ParameterTypeScriptingPath(String key, String description, String defaultFileName, SetupTester tester) {
        super(key, description, (String)null, defaultFileName);
        this.tester = tester;
    }

    public SetupTester getSetupTester() {
        return this.tester;
    }
}