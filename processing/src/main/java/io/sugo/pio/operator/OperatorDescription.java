package io.sugo.pio.operator;

import java.lang.reflect.InvocationTargetException;

/**
 */
public class OperatorDescription {
    private final String key;
    private final Class<? extends Operator> clazz;

    private boolean enabled = true;

    public OperatorDescription(String key, Class<? extends Operator> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public Class<? extends Operator> getOperatorClass() {
        return clazz;
    }

    public void disable() {
        this.enabled = false;
    }

    /**
     * Some operators may be disabled, e.g. because they cannot be applied inside an application
     * server (file access etc.)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Creates a new operator based on the description. Subclasses that want to overwrite the
     * creation behavior should override
     */
    public final Operator createOperatorInstance() throws OperatorCreationException {
        if (!isEnabled()) {
            throw new OperatorCreationException(OperatorCreationException.OPERATOR_DISABLED_ERROR,
                    key + "(" + clazz.getName() + ")", null);
        }
        Operator operator = null;
        try {
            operator = createOperatorInstanceByDescription(this);
        } catch (InstantiationException e) {
            throw new OperatorCreationException(OperatorCreationException.INSTANTIATION_ERROR,
                    key + "(" + clazz.getName() + ")", e);
        } catch (IllegalAccessException e) {
            throw new OperatorCreationException(OperatorCreationException.ILLEGAL_ACCESS_ERROR,
                    key + "(" + clazz.getName() + ")", e);
        } catch (NoSuchMethodException e) {
            throw new OperatorCreationException(OperatorCreationException.NO_CONSTRUCTOR_ERROR,
                    key + "(" + clazz.getName() + ")", e);
        } catch (InvocationTargetException e) {
            throw new OperatorCreationException(OperatorCreationException.CONSTRUCTION_ERROR,
                    key + "(" + clazz.getName() + ")", e);
        } catch (Throwable t) {
            throw new OperatorCreationException(OperatorCreationException.INSTANTIATION_ERROR, "(" + clazz.getName() + ")",
                    t);
        }
        return operator;
    }

    /**
     * This method creates the actual instance of the {@link Operator} defined by the given
     * {@link OperatorDescription}. Subclasses might overwrite this method in order to change the
     * creation behavior or way.
     */
    protected Operator createOperatorInstanceByDescription(final OperatorDescription description)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        java.lang.reflect.Constructor<? extends Operator> constructor = clazz
                .getConstructor(new Class[] { OperatorDescription.class });
        return constructor.newInstance(new Object[] { description });
    }

}
