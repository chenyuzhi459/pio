package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.server.http.dto.OperatorDto;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.utils.JsonUtil;
import org.codehaus.jettison.json.JSONObject;

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
        try {
            return Response.ok(processManager.getOperatorMetaJson()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{processId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addOperator(
            @PathParam("processId") final String processId,
            OperatorDto dto
    ) {
        try {
            Preconditions.checkNotNull(processId, "processId cannot be null");
            Preconditions.checkNotNull(dto.getOperatorType(), "operatorType cannot be null");
            OperatorProcess process = processManager.addOperator(processId, dto);
            return Response.ok(process).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Path("/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId
    ) {
        try {
            Operator operator = processManager.getOperator(processId, operatorId);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }


    @DELETE
    @Path("/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId
    ) {
        try {
            OperatorProcess process = processManager.deleteOperator(processId, operatorId);
            return Response.ok(process).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/connect/{processId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response connect(@PathParam("processId") final String processId, Connection dto) {
        try {
            Preconditions.checkNotNull(dto.getFromOperator(), "fromOperator cannot be null");
            Preconditions.checkNotNull(dto.getFromPort(), "fromPort cannot be null");
            Preconditions.checkNotNull(dto.getToOperator(), "toOperator cannot be null");
            Preconditions.checkNotNull(dto.getToPort(), "toPort cannot be null");
            OperatorProcess process = processManager.connect(processId, dto);
            return Response.ok(process).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/disconnect/{processId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response disconnect(@PathParam("processId") final String processId, Connection dto) {
        try {
            Preconditions.checkNotNull(dto.getFromOperator(), "fromOperator cannot be null");
            Preconditions.checkNotNull(dto.getFromPort(), "fromPort cannot be null");
            Preconditions.checkNotNull(dto.getToOperator(), "toOperator cannot be null");
            Preconditions.checkNotNull(dto.getToPort(), "toPort cannot be null");
            OperatorProcess process = processManager.disconnect(processId, dto);
            return Response.ok(process).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateParameter(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId,
            String query
    ) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String key = JsonUtil.getString(jsonObject, "key");
            String value = JsonUtil.getString(jsonObject, "value");

            Operator operator = processManager.updateParameter(processId, operatorId, key, value);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/update/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateOperator(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId,
            OperatorDto dto
    ) {
        try {
            Operator operator = processManager.updateOperator(processId, operatorId, dto);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }
}
