package io.sugo.pio.server;

import com.google.inject.Inject;
import io.sugo.pio.client.CachingClusteredClient;
import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.query.QueryWalker;

/**
 */
public class ClientQueryWalker implements QueryWalker {
    private final CachingClusteredClient baseClient;

    @Inject
    public ClientQueryWalker(
            CachingClusteredClient baseClient
    ) {
        this.baseClient = baseClient;
    }

    @Override
    public <Q, R> QueryRunner<Q, R> getQueryRunner() {
        return baseClient;
    }
}
