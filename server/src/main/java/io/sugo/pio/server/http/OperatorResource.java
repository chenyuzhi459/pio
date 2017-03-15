package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.server.http.dto.OperatorDto;
import io.sugo.pio.server.http.dto.OperatorMetadataDto;
import io.sugo.pio.server.http.dto.OperatorParamDto;
import io.sugo.pio.server.process.ProcessManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/pio/operator/")
public class OperatorResource {
    private static final Logger log = new Logger(OperatorResource.class);
    private final ObjectMapper jsonMapper;
    private final ProcessManager processManager;
    private final JavaType operatorParamType;
    private final JavaType operatorMetadataType;

    @Inject
    public OperatorResource(
            @Json ObjectMapper jsonMapper,
            ProcessManager processManager
    ) {
        this.jsonMapper = jsonMapper;
        this.processManager = processManager;
        this.operatorParamType = jsonMapper.getTypeFactory().constructParametrizedType(List.class, ArrayList.class, OperatorParamDto.class);
        this.operatorMetadataType = jsonMapper.getTypeFactory().constructParametrizedType(List.class, ArrayList.class, OperatorMetadataDto.class);
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
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateParameter(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId,
            String keyValues
    ) {
        try {
            List<OperatorParamDto> paramList = jsonMapper.readValue(keyValues, operatorParamType);
            Operator operator = processManager.updateParameter(processId, operatorId, paramList);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
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
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/result/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getResult(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId
    ) {
        try {
            Operator operator = processManager.getOperator(processId, operatorId);
            return Response.ok(operator.getResult()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/metadata/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateMetadata(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId,
            String metadata
    ) {
        try {
            List<OperatorMetadataDto> metadataList = jsonMapper.readValue(metadata, operatorMetadataType);
            Operator operator = processManager.updateMetadata(processId, operatorId, metadataList);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
