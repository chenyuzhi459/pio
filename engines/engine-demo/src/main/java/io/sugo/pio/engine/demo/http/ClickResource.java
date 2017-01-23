package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import io.sugo.pio.engine.demo.Click;
import io.sugo.pio.engine.training.Algorithm;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Path("query/click")
public class ClickResource {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private static final Map<String, EvictingQueue<Click>> clickMap = new HashMap<>();

    @POST
    @Path("/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submit(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            Click click = jsonMapper.readValue(in, Click.class);
            EvictingQueue<Click> clicks = clickMap.get(click.getUserId());
            if (null == clicks) {
                clicks = EvictingQueue.create(5);
                clickMap.put(click.getUserId(), clicks);
            }

            clicks.add(click);
            return Response.status(Response.Status.ACCEPTED).entity("ok").build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }

    @POST
    @Path("/request")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response request(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            ClickQuery query = jsonMapper.readValue(in, ClickQuery.class);
            EvictingQueue<Click> clicks = clickMap.get(query.getUserId());

            return Response.status(Response.Status.ACCEPTED).entity("ok").build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }

    static class ClickQuery {
        private String userId;
        private List<String> items;

        @JsonCreator
        public ClickQuery(@JsonProperty("userId") String userId, @JsonProperty("items") List<String> items) {
            this.userId = userId;
            this.items = items;
        }

        @JsonProperty
        public String getUserId() {
            return userId;
        }

        @JsonProperty
        public List<String> getItems() {
            return items;
        }
    }
}
