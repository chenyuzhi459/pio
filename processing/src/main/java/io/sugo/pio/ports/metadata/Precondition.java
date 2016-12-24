package io.sugo.pio.ports.metadata;

/**
 */
public interface Precondition {
    /**
     * Checks whether the precondition is satisfied.
     *
     * @param metaData
     *            the delivered meta data. Note that this may differ from the meta data currently
     *            assigned to the input port for which this Precondition was created, e.g. for a
     *            ClooectionPrecondition.
     * */
    public void check(MetaData metaData);

    /** Returns true if the given object is compatible with this precondition. */
    public boolean isCompatible(MetaData input);
}
