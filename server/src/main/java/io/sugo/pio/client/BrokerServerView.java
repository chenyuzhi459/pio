package io.sugo.pio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import com.metamx.http.client.HttpClient;
import io.sugo.pio.client.selector.QueryablePioServer;
import io.sugo.pio.client.selector.ServerSelector;
import io.sugo.pio.client.selector.TierSelectorStrategy;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.guice.annotations.Client;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.query.QueryRunner;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 */
public class BrokerServerView implements ServerView {
    private static final Logger log = new Logger(BrokerServerView.class);

    private final Object lock = new Object();

    private final ConcurrentMap<String, QueryablePioServer> clients;
    private final Map<String, ServerSelector> selectors;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final TierSelectorStrategy tierSelectorStrategy;
    private final FilteredServerInventoryView baseView;

    private volatile boolean initialized = false;

    @Inject
    public BrokerServerView(
            @Client HttpClient httpClient,
            @Json ObjectMapper objectMapper,
            FilteredServerInventoryView baseView,
            TierSelectorStrategy tierSelectorStrategy
    )
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseView = baseView;
        this.tierSelectorStrategy = tierSelectorStrategy;
        this.clients = Maps.newConcurrentMap();
        this.selectors = Maps.newHashMap();

        ExecutorService exec = Execs.singleThreaded("BrokerServerView-%s");
        baseView.registerServerCallback(
                exec,
                new ServerView.ServerCallback()
                {
                    @Override
                    public ServerView.CallbackAction serverRemoved(PioServer server)
                    {
                        removeServer(server);
                        return ServerView.CallbackAction.CONTINUE;
                    }

                    @Override
                    public CallbackAction serverAdded(PioServer server) {
                        addServer(server);
                        return ServerView.CallbackAction.CONTINUE;
                    }

                    @Override
                    public CallbackAction serverUpdated(PioServer oldServer, PioServer newServer) {
                        updateServer(oldServer, newServer);
                        return ServerView.CallbackAction.CONTINUE;
                    }
                }
        );
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void clear()
    {
        synchronized (lock) {
            final Iterator<String> clientsIter = clients.keySet().iterator();
            while (clientsIter.hasNext()) {
                clientsIter.remove();
            }

            final Iterator<ServerSelector> selectorsIter = selectors.values().iterator();
            while (selectorsIter.hasNext()) {
                final ServerSelector selector = selectorsIter.next();
                selectorsIter.remove();
                while (!selector.isEmpty()) {
                    final QueryablePioServer pick = selector.pick();
                    selector.removeServer(pick);
                }
            }
        }
    }

    private void addServer(PioServer server)
    {
        synchronized (lock) {
            ServerSelector selector = selectors.get(server.getType());
            if (selector == null) {
                selector = new ServerSelector(tierSelectorStrategy);
                selectors.put(server.getType(), selector);
            }

            QueryablePioServer queryablePioServer = clients.get(server.getName());
            if (queryablePioServer == null) {
                queryablePioServer = new QueryablePioServer(baseView.getInventoryValue(server.getName()), makeDirectClient(server));
                QueryablePioServer exists = clients.put(server.getName(), queryablePioServer);
                if (exists != null) {
                    log.warn("QueryRunner for server[%s] already existed!? Well it's getting replaced", server);
                }
            }
            selector.addServer(queryablePioServer);
        }
    }

    private void updateServer(PioServer oldServer, PioServer newServer) {
        synchronized (lock) {
            ServerSelector oldSelector = selectors.get(oldServer.getType());
            QueryablePioServer oldQueryablePioServer = clients.get(oldServer.getName());
            if (oldQueryablePioServer != null) {
                clients.remove(oldServer.getName());
                if (null != oldSelector) {
                    oldSelector.removeServer(oldQueryablePioServer);
                }
            }

            QueryablePioServer queryablePioServer = new QueryablePioServer(baseView.getInventoryValue(newServer.getName()), makeDirectClient(newServer));
            clients.put(newServer.getName(), queryablePioServer);

            ServerSelector newSelector = selectors.get(newServer.getType());
            if (newSelector == null) {
                newSelector = new ServerSelector(tierSelectorStrategy);
                selectors.put(newServer.getType(), newSelector);
            }
            newSelector.addServer(queryablePioServer);
        }
    }

    private DirectPioClient makeDirectClient(PioServer server)
    {
        return new DirectPioClient(objectMapper, httpClient, server.getHost());
    }

    private QueryablePioServer removeServer(PioServer server)
    {
        return clients.remove(server.getName());
    }

    public ServerSelector getServerSelector(String serverType) {
        return selectors.get(serverType);
    }

    public <Q, R> QueryRunner<Q, R> getQueryRunner(PioServer server)
    {
        synchronized (lock) {
            QueryablePioServer queryablePioServer = clients.get(server.getName());
            if (queryablePioServer == null) {
                log.error("WTF?! No QueryablePioServer found for %s", server.getName());
                return null;
            }
            return queryablePioServer.getClient();
        }
    }

    @Override
    public void registerServerCallback(Executor exec, ServerCallback callback)
    {
        baseView.registerServerCallback(exec, callback);
    }
}
