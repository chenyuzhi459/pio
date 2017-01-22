package io.sugo.pio.client;

import com.google.inject.Inject;
import io.sugo.pio.client.selector.QueryablePioServer;
import io.sugo.pio.client.selector.ServerSelector;
import io.sugo.pio.query.Query;
import io.sugo.pio.query.QueryRunner;

import java.util.Map;

/**
 */
public class CachingClusteredClient<Q, R> implements QueryRunner<Q, R> {
    private final BrokerServerView brokerServerView;

    @Inject
    public CachingClusteredClient(BrokerServerView brokerServerView) {
        this.brokerServerView = brokerServerView;
    }

    @Override
    public R run(Query<Q> query, Map<String, Object> responseContext) {
        ServerSelector selector = brokerServerView.getServerSelector(query.getType());
        QueryablePioServer queryablePioServer = selector.pick();
        PioServer server = queryablePioServer.getServer();
        QueryRunner<Q, R> queryRunner = brokerServerView.getQueryRunner(server);
        return queryRunner.run(query, responseContext);
    }
}
