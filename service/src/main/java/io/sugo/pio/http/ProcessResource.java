package io.sugo.pio.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.process.ResponseMsg;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Path("/pio/process/")
public class ProcessResource {
    private static final Logger log = new Logger(ProcessResource.class);
    private final ObjectMapper jsonMapper;
    private final ProcessManager processManager;

    @Inject
    public ProcessResource(
            @Json ObjectMapper jsonMapper,
            ProcessManager processManager
    ) {
        this.jsonMapper = jsonMapper;
        this.processManager = processManager;
    }

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {
        return Response.ok("info").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @Context final HttpServletRequest req
    ) {
        ResponseMsg msg = new ResponseMsg();
        try {
            final OperatorProcess process = jsonMapper.readValue(in, OperatorProcess.class);
            msg.put("id", processManager.register(process));
            msg.status(Response.Status.ACCEPTED);
        } catch (IOException e) {
            log.warn(e, "");
            msg.error(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.status(msg.status()).entity(msg).build();
    }

    @GET
    @Path("/{id}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus(@PathParam("id") final String id) {
        OperatorProcess process = processManager.get(id);
        if (process == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ImmutableMap.of("error", String.format("[%s] does not exist", id)))
                    .build();
        } else {
            return Response.ok(process).build();
        }
    }

    @GET
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperatorMetadata() {
        return Response.ok().build();
    }
}
