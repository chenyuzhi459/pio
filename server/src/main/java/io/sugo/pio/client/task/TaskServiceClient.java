package io.sugo.pio.client.task;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.common.ISE;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.Request;
import com.metamx.http.client.response.InputStreamResponseHandler;
import io.sugo.pio.client.selector.Server;
import io.sugo.pio.curator.discovery.ServerDiscoverySelector;
import io.sugo.pio.guice.annotations.Global;
import org.jboss.netty.handler.codec.http.HttpMethod;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 */
public class TaskServiceClient {
    private static final InputStreamResponseHandler RESPONSE_HANDLER = new InputStreamResponseHandler();

    private final HttpClient client;
    private final ServerDiscoverySelector selector;


    @Inject
    public TaskServiceClient(
            @Global HttpClient client,
            @TaskService ServerDiscoverySelector selector
    ) {
        this.client = client;
        this.selector = selector;
    }

    public InputStream submitTask(String taskJson) {
        return runQuery(taskJson);
    }

    private InputStream runQuery(String queryJson) {
        try {
            return client.go(
                    new Request(
                            HttpMethod.POST,
                            new URL(String.format("%s/task", baseUrl()))
                    ).setContent(MediaType.APPLICATION_JSON, queryJson.getBytes()),
                    RESPONSE_HANDLER
            ).get();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private String baseUrl() {
        try {
            final Server instance = selector.pick();
            if (instance == null) {
                throw new ISE("Cannot find instance of indexingService");
            }

            return new URI(
                    instance.getScheme(),
                    null,
                    instance.getAddress(),
                    instance.getPort(),
                    "/pio/overlord",
                    null,
                    null
            ).toString();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
