package io.sugo.pio.server.process;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.smile.SmileMediaTypes;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.guice.annotations.Json;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;

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

    @POST
    @Produces({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
    @Consumes({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty
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
    @Produces({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
    @Consumes({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
    public Response getStatus(
            InputStream in,
            @QueryParam("pretty") String pretty
    ) {
        ResponseMsg msg = new ResponseMsg();
        try {
            final ProcessBuilder handler = jsonMapper.readValue(in, ProcessBuilder.class);
            Process process = handler.getProcess();
            msg.put("id", process.getId());
            msg.status(Response.Status.ACCEPTED);
            processManager.register(process);
        } catch (Exception e) {
            log.warn(e, "");
            msg.error(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.status(msg.status()).entity(msg).build();
    }
}
