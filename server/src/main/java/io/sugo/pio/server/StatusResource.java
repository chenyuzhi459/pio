package io.sugo.pio.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
@Path("/status")
public class StatusResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        return Response.ok("ok").build();
    }
}
