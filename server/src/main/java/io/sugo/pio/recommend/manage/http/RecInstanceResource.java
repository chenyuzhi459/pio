package io.sugo.pio.recommend.manage.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.recommend.RecommendManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pio/manage/recommend")
public class RecInstanceResource {
    private static final Logger log = new Logger(RecInstanceResource.class);
    private final ObjectMapper jsonMapper;
    private final RecommendManager recommendManager;

    @Inject
    public RecInstanceResource(
            @Json ObjectMapper jsonMapper,
            RecommendManager recommendManager
    ) {
        this.jsonMapper = jsonMapper;
        this.recommendManager = recommendManager;
    }

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {
        return Response.ok("recommend info").build();
    }

}
