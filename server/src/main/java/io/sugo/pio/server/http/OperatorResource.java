package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.server.http.dto.OperatorDto;
import io.sugo.pio.server.process.ProcessManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pio/operator/")
public class OperatorResource {
    private static final Logger log = new Logger(OperatorResource.class);
    private final ObjectMapper jsonMapper;
    private final ProcessManager processManager;

    @Inject
    public OperatorResource(
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
        return Response.ok("operator info").build();
    }

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response operators() {
        return Response.ok(processManager.getOperatorMetaJson()).build();
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addOperator(OperatorDto dto) {
        try {
            Preconditions.checkNotNull(dto.getProcessId(), "processId cannot be null");
            Preconditions.checkNotNull(dto.getOperatorType(), "operatorType cannot be null");
            Operator op = processManager.addOperator(dto);
            return Response.ok(op).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }
}
