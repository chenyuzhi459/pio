package io.sugo.pio.ports.metadata;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.error.ProcessSetupError;
import io.sugo.pio.ports.PortOwner;

import java.text.MessageFormat;

public class SimpleProcessSetupError implements ProcessSetupError {

    private static final MessageFormat formatter = new MessageFormat("");

    private String i18nKey;
    private final Object[] i18nArgs;
    private final PortOwner owner;
    private final Severity severity;

    public SimpleProcessSetupError(Severity severity, PortOwner owner, String i18nKey, Object... i18nArgs) {
        super();
        this.i18nKey = i18nKey;
        this.i18nArgs = i18nArgs;
        this.owner = owner;
        this.severity = severity;
    }

    @Override
    public final String getMessage() {
        String message = I18N.getErrorMessage(i18nKey);
        try {
            formatter.applyPattern(message);
            String formatted = formatter.format(i18nArgs);
            return formatted;
        } catch (Throwable t) {
            return message;
        }
    }

    @Override
    public final PortOwner getOwner() {
        return owner;
    }

    @Override
    public final Severity getSeverity() {
        return severity;
    }

}
