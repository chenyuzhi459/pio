package io.sugo.pio.client.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.Request;
import com.metamx.http.client.response.InputStreamResponseHandler;
import io.sugo.pio.client.BrokerServerView;
import io.sugo.pio.client.PioServer;
import io.sugo.pio.client.selector.QueryablePioServer;
import io.sugo.pio.client.selector.ServerSelector;
import io.sugo.pio.curator.discovery.ServerDiscoverySelector;
import io.sugo.pio.guice.annotations.Global;
import org.jboss.netty.handler.codec.http.HttpMethod;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.net.URL;

public class EngineTaskClient {
    private static final InputStreamResponseHandler RESPONSE_HANDLER = new InputStreamResponseHandler();

    private final BrokerServerView brokerServerView;
    private final HttpClient client;
    private final ObjectMapper jsonMapper;
    private final ServerDiscoverySelector selector;

    @Inject
    public EngineTaskClient(
            BrokerServerView brokerServerView,
            @Global HttpClient client,
            ObjectMapper jsonMapper,
            @BrokerService ServerDiscoverySelector selector
    ) {
        this.client = client;
        this.jsonMapper = jsonMapper;
        this.selector = selector;
        this.brokerServerView = brokerServerView;
    }

    public InputStream runQuery(Object queryObject, String type) {
        try {
            ServerSelector selector = brokerServerView.getServerSelector(type);
            QueryablePioServer queryablePioServer = selector.pick();
            PioServer server = queryablePioServer.getServer();

            InputStream inputStream = client.go(
                    new Request(
                            HttpMethod.POST,
                            new URL(String.format("http://%s/pio/v1", server.getHost()))
                    ).setContent(MediaType.APPLICATION_JSON, jsonMapper.writeValueAsBytes(queryObject)),
                    RESPONSE_HANDLER
            ).get();
            return inputStream;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
