package io.sugo.pio.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.sugo.pio.Process;
import io.sugo.pio.guice.annotations.Json;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Path("/pio/process/")
public class ProcessResource {
    private final ObjectMapper jsonMapper;

    @Inject
    public ProcessResource(
            @Json ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty
    ) {
        ResponseMsg msg = new ResponseMsg();
        try {
            final Process process = jsonMapper.readValue(in, Process.class);
            msg.put("id", process.getId());
            msg.status(Response.Status.ACCEPTED);
            process.run();
        } catch (IOException e) {
            msg.error(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.status(msg.status()).entity(msg).build();
    }
}
