package io.sugo.pio.operator;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 */
public abstract class ResultObjectAdapter extends AbstractIOObject implements ResultObject {
    private static final long serialVersionUID = -8621885253590411373L;
    private Annotations annotations = new Annotations();

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (annotations == null) {
            annotations = new Annotations();
        }
    }

    protected void cloneAnnotationsFrom(IOObject other) {
        this.annotations = other.getAnnotations().clone();
    }

    /** The default implementation returns the classname without package. */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * The default implementation simply returns the result of the method {@link #toString()}.
     */
    @Override
    public String toResultString() {
        return toString();
    }

    @Override
    public Annotations getAnnotations() {
        return this.annotations;
    }
}
