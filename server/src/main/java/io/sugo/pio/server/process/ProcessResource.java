package io.sugo.pio.server.process;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jaxrs.smile.SmileMediaTypes;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.metadata.MetadataProcessInstanceManager;

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
            final ProcessBuilder handler = jsonMapper.readValue(in, ProcessBuilder.class);
            Process process = handler.getProcess();
            msg.put("id", process.getId());
            msg.status(Response.Status.ACCEPTED);
            processManager.register(process);
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
        ProcessInstance pi = processManager.get(id);
        if (pi == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ImmutableMap.of("error", String.format("[%s] does not exist", id)))
                    .build();
        } else {
            return Response.ok(pi).build();
        }
    }
}
