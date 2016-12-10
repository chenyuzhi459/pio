package sugo.io.pio.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.guice.annotations.Json;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 */
@Path("/pio/engine/")
public class EngineResource {
    private final ObjectMapper jsonMapper;

    @Inject
    public EngineResource(@Json ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServer()
    {
        return Response.status(Response.Status.ACCEPTED).entity("Hello world").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
        InputStream in,
        @QueryParam("pretty") String pretty,
        @Context final HttpServletRequest req
    ) {
        final String reqContentType = req.getContentType();
        final ObjectWriter jsonWriter = pretty != null
                ? jsonMapper.writerWithDefaultPrettyPrinter()
                : jsonMapper.writer();

        final BatchEventHose eventHose;
//        try {
//            eventHose = jsonMapper.readValue(in, BatchEventHose.class);
//            System.out.println(eventHose);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return Response.status(Response.Status.ACCEPTED).entity("Hello world").build();
    }
}
