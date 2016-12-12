package sugo.io.pio.server;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.engine.Engine;
import sugo.io.pio.guice.annotations.Json;
import sugo.io.pio.metadata.SQLMetadataEngineStorage;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Path("/pio/engine/")
public class EngineResource {
    private final ObjectMapper jsonMapper;
    private final SQLMetadataEngineStorage engineStorage;

    @Inject
    public EngineResource(
            @Json ObjectMapper jsonMapper,
            SQLMetadataEngineStorage engineStorage) {
        this.jsonMapper = jsonMapper;
        this.engineStorage = engineStorage;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEngine(@PathParam("id") String engineId) {
        final ObjectWriter jsonWriter = jsonMapper.writer();
        final Optional<String> response = engineStorage.get(engineId).transform(new Function<Engine, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Engine engine) {
                try {
                    return jsonWriter.writeValueAsString(engine);
                } catch (JsonProcessingException e) {
                    return "Failed to serialize the engine";
                }
            }
        });

        return Response.status(Response.Status.ACCEPTED).entity(response.or("Engine not existed")).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            final Engine engine = jsonMapper.readValue(in, Engine.class);
            engineStorage.register(engine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.ACCEPTED).entity("Success").build();
    }
}
