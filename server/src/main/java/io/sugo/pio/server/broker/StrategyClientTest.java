package io.sugo.pio.server.broker;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class StrategyClientTest {
    private ClientConfig clientConfig;
    private Client client;

    public StrategyClientTest() {
        clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        client = Client.create(clientConfig);
    }

    @SuppressWarnings("unchecked")
    private String doGet(String serviceUrl, String path, MultivaluedMap<String, String> params) {
        WebResource resource = client.resource(serviceUrl + path);
        if (params != null && !params.isEmpty()) {
            resource = resource.queryParams(params);
        }
        resource = resource.queryParam("map", Boolean.TRUE + "");

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String result;
        if (response.getStatus() == 503) {
            throw new RuntimeException("Service not available on " + serviceUrl);
        }
        if (response.getStatus() != 200) {
            return response.getEntity(String.class);
        }

        String jsonStr = response.getEntity(String.class);
        return jsonStr;
    }
}
