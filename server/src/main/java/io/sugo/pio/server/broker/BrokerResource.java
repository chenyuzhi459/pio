package io.sugo.pio.server.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.io.CountingOutputStream;
import com.google.inject.Inject;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.query.QueryWalker;
import io.sugo.pio.recommend.bean.RecStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 */
@Path("/pio/broker/")
public class BrokerResource {
    private static final int RESPONSE_CTX_HEADER_LEN_LIMIT = 7*1024;

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
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) throws IOException {
        final long start = System.currentTimeMillis();
        RecStrategy strategy = null;

        final String contentType = MediaType.APPLICATION_JSON;
        final ObjectWriter jsonWriter = pretty != null
                ? jsonMapper.writerWithDefaultPrettyPrinter()
                : jsonMapper.writer();

        try {
            strategy = jsonMapper.readValue(in, RecStrategy.class);
            final List<String> data = runner.run(strategy);

            Response.ResponseBuilder builder = Response
                    .ok(
                            new StreamingOutput() {
                                @Override
                                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                                    // json serializer will always close the yielder
                                    CountingOutputStream os = new CountingOutputStream(outputStream);
                                    jsonWriter.writeValue(os, data);

                                    os.flush(); // Some types of OutputStream suppress flush errors in the .close() method.
                                    os.close();

                                    final long queryTime = System.currentTimeMillis() - start;
                                }
                            },
                            contentType
                    );

            return builder
                    .build();
        } catch (Exception e) {
            return Response.serverError().type(contentType).entity(
                    jsonWriter.writeValueAsBytes(
                            ImmutableMap.of(
                                    "error", e.getMessage() == null ? "null exception" : e.getMessage()
                            )
                    )
            ).build();
        }
    }
}
