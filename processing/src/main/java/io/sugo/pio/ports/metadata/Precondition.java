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

    /** Returns a human readable description. */
    public String getDescription();

    /** Returns true if the given object is compatible with this precondition. */
    public boolean isCompatible(MetaData input, CompatibilityLevel level);

    /**
     * Assume that the precondition is satisfied, i.e., artificially generate compatible meta data
     * at the input port. This method is used to check what output would be generated if the input
     * was correctly delivered.
     */
    public void assumeSatisfied();

    /** Returns the meta data required by this precondition. */
    public MetaData getExpectedMetaData();
}
