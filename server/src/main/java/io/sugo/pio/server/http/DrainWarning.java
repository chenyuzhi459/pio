package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.server.http.dto.OperatorParamDto;
import io.sugo.pio.server.process.ProcessManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pio/process/drain/")
public class DrainWarning {
    private static final Logger log = new Logger(DrainWarning.class);
    private final ObjectMapper jsonMapper;
    private final ProcessManager processManager;

    private static final String PROCESS_ID = "ef26b0f9-131a-4d7f-8417-b013a1123383";
    private static final String OPERATOR_ID_APPLY_MODEL = "apply_model-a035a434-b36b-47f3-8929-6ca5e917455d";
    private static final String OPERATOR_ID_SELECT_ATTRIBUTES = "select_attributes-56a88b68-95d0-4ede-8cd0-fb449f3156c2";
    private static final String OPERATOR_ID_CSV_TRAIN = "read_csv-dce6ea3a-79cd-4a46-a6f6-e61b9e5decc7";
    private static final String OPERATOR_ID_CSV_PREDICT = "read_csv-0e5bcebf-0bc2-4a5e-b4c0-61169b01cba3";

    @Inject
    public DrainWarning(
            @Json ObjectMapper jsonMapper,
            ProcessManager processManager
    ) {
        this.jsonMapper = jsonMapper;
        this.processManager = processManager;
    }

    @POST
    @Path("/param/{type}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateParameter(@PathParam("type") final String type, List<OperatorParamDto> paramList) {
        String operatorId;
        if ("train".equals(type)) {
            operatorId = OPERATOR_ID_CSV_TRAIN;
        } else {
            operatorId = OPERATOR_ID_CSV_PREDICT;
        }

        try {
            Operator operator = processManager.updateParameter(PROCESS_ID, operatorId, paramList);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/data{type}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateExampleData(@PathParam("type") final String type, List<String> dataList) {
        String operatorId;
        if ("train".equals(type)) {
            operatorId = OPERATOR_ID_CSV_TRAIN;
        } else {
            operatorId = OPERATOR_ID_CSV_PREDICT;
        }

        try {
            Operator operator = processManager.updateExampleData(PROCESS_ID, operatorId, dataList);
            return Response.ok(operator).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/run")
    @Produces({MediaType.APPLICATION_JSON})
    public Response run() {
        try {
            OperatorProcess process = processManager.run(PROCESS_ID);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/result")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getResult() {
        try {
            Operator operator = processManager.getOperator(PROCESS_ID, OPERATOR_ID_APPLY_MODEL);
            return Response.ok(operator.getResult()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
