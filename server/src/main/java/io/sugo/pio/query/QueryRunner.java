package io.sugo.pio.query;

import java.util.Map;

/**
 */
public interface QueryRunner<Q, R>
{
  /**
   * Runs the given query and returns results in a time-ordered sequence
   * @param query
   * @param responseContext
   * @return
   */
  R run(Query<Q> query, Map<String, Object> responseContext);
}
