package io.sugo.pio.server.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.query.QueryWalker;
import io.sugo.pio.recommend.bean.RecStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 */
@Path("/pio/broker/")
public class BrokerResource {
    private static final int RESPONSE_CTX_HEADER_LEN_LIMIT = 7 * 1024;

    private final QueryWalker texasRanger;
    private final ObjectMapper jsonMapper;
    private final StrategyRunner runner;

    @Inject
    public BrokerResource(
            @Json ObjectMapper jsonMapper,
            QueryWalker texasRanger,
            StrategyRunner runner
    ) {
        this.texasRanger = texasRanger;
        this.jsonMapper = jsonMapper;
        this.runner = runner;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @Context final HttpServletRequest req
    ) throws IOException {
        try {
            RecStrategy strategy = jsonMapper.readValue(in, RecStrategy.class);
            final List<String> data = runner.run(strategy);
            return Response.ok(data).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
