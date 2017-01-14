package io.sugo.pio.operator;

/**
 */
public interface ResultObject extends IOObject {
    /** Defines the name of this result object. */
    public abstract String getName();

    /** Result string will be displayed in result files written with a ResultWriter operator. */
    public abstract String toResultString();
}
