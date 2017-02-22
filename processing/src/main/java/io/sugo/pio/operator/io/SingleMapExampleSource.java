package io.sugo.pio.operator.io;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;

public class SingleMapExampleSource extends AbstractExampleSource {

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        return null;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.SingleMapExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.SingleMapExampleSource.description");
    }
}
