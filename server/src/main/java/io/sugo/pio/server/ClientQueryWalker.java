package io.sugo.pio.server;

import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.query.QueryWalker;

/**
 */
public class ClientQueryWalker implements QueryWalker {

    @Override
    public <Q, R> QueryRunner<Q, R> getQueryRunner() {
        return null;
    }
}
