package io.sugo.pio.server;

import com.google.inject.Inject;
import io.sugo.pio.query.QueryWalker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
@Path("/pio/v2/")
public class QueryResource {
    private final QueryWalker texasRanger;

    @Inject
    public QueryResource(QueryWalker texasRanger) {
        this.texasRanger = texasRanger;
    }

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {
        return Response.ok("info").build();
    }


}
