package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.server.http.dto.OperatorDto;
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

    @Inject
    public OperatorResource(
            @Json ObjectMapper jsonMapper,
            ProcessManager processManager
    ) {
        this.jsonMapper = jsonMapper;
        this.processManager = processManager;
        this.operatorParamType = jsonMapper.getTypeFactory().constructParametrizedType(List.class, ArrayList.class, OperatorParamDto.class);
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
            Preconditions.checkNotNull(processId, I18N.getMessage("pio.error.operator.process_id_can_not_null"));
            Preconditions.checkNotNull(dto.getOperatorType(), I18N.getMessage("pio.error.operator.type_can_not_null"));
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
            Preconditions.checkNotNull(dto.getFromOperator(), I18N.getMessage("pio.error.operator.from_can_not_null"));
            Preconditions.checkNotNull(dto.getFromPort(), I18N.getMessage("pio.error.operator.from_port_can_not_null"));
            Preconditions.checkNotNull(dto.getToOperator(), I18N.getMessage("pio.error.operator.to_can_not_null"));
            Preconditions.checkNotNull(dto.getToPort(), I18N.getMessage("pio.error.operator.to_port_can_not_null"));
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
            Preconditions.checkNotNull(dto.getFromOperator(), I18N.getMessage("pio.error.operator.from_can_not_null"));
            Preconditions.checkNotNull(dto.getFromPort(), I18N.getMessage("pio.error.operator.from_port_can_not_null"));
            Preconditions.checkNotNull(dto.getToOperator(), I18N.getMessage("pio.error.operator.to_can_not_null"));
            Preconditions.checkNotNull(dto.getToPort(), I18N.getMessage("pio.error.operator.to_port_can_not_null"));
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
            List<OperatorParamDto> paramList
    ) {
        try {
//            List<OperatorParamDto> paramList = jsonMapper.readValue(keyValues, operatorParamType);
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
    @Path("/data/{processId}/{operatorId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateExampleData(
            @PathParam("processId") final String processId,
            @PathParam("operatorId") final String operatorId,
            List<String> dataList
    ) {
        try {
            Operator operator = processManager.updateExampleData(processId, operatorId, dataList);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
