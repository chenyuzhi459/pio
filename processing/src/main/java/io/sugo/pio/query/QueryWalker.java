package io.sugo.pio.query;

/**
 */
public interface QueryWalker {
    /**
     * Gets the Queryable for a given interval, the Queryable returned can be any version(s) or partitionNumber(s)
     * such that it represents the interval.
     */
    public <Q, R> QueryRunner<Q, R> getQueryRunner();
}
