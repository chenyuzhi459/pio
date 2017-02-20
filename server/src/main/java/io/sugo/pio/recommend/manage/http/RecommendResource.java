package io.sugo.pio.recommend.manage.http;


import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.recommend.RecommendProxy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pio/rec")
public class RecommendResource {
    private static final Logger log = new Logger(RecommendResource.class);
    private final RecommendProxy recProxy;

    @Inject
    public RecommendResource(
            RecommendProxy recProxy
    ) {
        this.recProxy = recProxy;
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response recommend(
            @PathParam("id") final String id,
            @Context final HttpServletRequest req
    ) {
        try {
            List<String> items = recProxy.recommend(id, req);
            return Response.ok(items).build();
        } catch (NullPointerException e) {
            log.error(e, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error(e, "recommend error with %s", id);
            return Response.serverError().build();
        }
    }
}
