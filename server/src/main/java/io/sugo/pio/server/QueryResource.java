package io.sugo.pio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.io.CountingOutputStream;
import com.google.inject.Inject;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.query.Query;
import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.query.QueryWalker;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 */
@Path("/pio/v1/")
public class QueryResource {
    private static final int RESPONSE_CTX_HEADER_LEN_LIMIT = 7*1024;

    private final QueryWalker texasRanger;
    private final ObjectMapper jsonMapper;

    @Inject
    public QueryResource(
            @Json ObjectMapper jsonMapper,
            QueryWalker texasRanger) {
        this.texasRanger = texasRanger;
        this.jsonMapper = jsonMapper;
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
        Query query = null;

        final String contentType = MediaType.APPLICATION_JSON;
        final ObjectWriter jsonWriter = pretty != null
                ? jsonMapper.writerWithDefaultPrettyPrinter()
                : jsonMapper.writer();

        try {
            query = jsonMapper.readValue(in, Query.class);
            final Map<String, Object> responseContext = new MapMaker().makeMap();
            final QueryRunner queryRunner = texasRanger.getQueryRunner();
            final Object res = queryRunner.run(query, responseContext);

            Response.ResponseBuilder builder = Response
                    .ok(
                            new StreamingOutput()
                            {
                                @Override
                                public void write(OutputStream outputStream) throws IOException, WebApplicationException
                                {
                                    // json serializer will always close the yielder
                                    CountingOutputStream os = new CountingOutputStream(outputStream);
                                    jsonWriter.writeValue(os, res);

                                    os.flush(); // Some types of OutputStream suppress flush errors in the .close() method.
                                    os.close();

                                    final long queryTime = System.currentTimeMillis() - start;
                                }
                            },
                            contentType
                    );

            //Limit the response-context header, see https://github.com/druid-io/druid/issues/2331
            //Note that Response.ResponseBuilder.header(String key,Object value).build() calls value.toString()
            //and encodes the string using ASCII, so 1 char is = 1 byte
            String responseCtxString = jsonMapper.writeValueAsString(responseContext);
            if (responseCtxString.length() > RESPONSE_CTX_HEADER_LEN_LIMIT) {
                responseCtxString = responseCtxString.substring(0, RESPONSE_CTX_HEADER_LEN_LIMIT);
            }

            return builder
                    .header("X-Druid-Response-Context", responseCtxString)
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
