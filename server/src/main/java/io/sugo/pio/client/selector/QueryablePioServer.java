package io.sugo.pio.client.selector;

import io.sugo.pio.client.DirectPioClient;
import io.sugo.pio.client.PioServer;

/**
 */
public class QueryablePioServer {
    private final PioServer server;
    private final DirectPioClient client;

    public QueryablePioServer(PioServer server, DirectPioClient client)
    {
        this.server = server;
        this.client = client;
    }

    public PioServer getServer()
    {
        return server;
    }

    public DirectPioClient getClient()
    {
        return client;
    }
}
