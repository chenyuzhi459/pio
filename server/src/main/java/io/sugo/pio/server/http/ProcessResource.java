package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.constant.ProcessConstant;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.utils.JsonUtil;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return Response.ok("process info").build();
    }

    @GET
    @Path("/list/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(@PathParam("tenantId") final String tenantId, @QueryParam("all") boolean includeDelete) {
        try {
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"));
            List<OperatorProcess> processes = processManager.getAll(tenantId, includeDelete);
            return Response.ok(processes).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/case/list/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllCases(@QueryParam("all") boolean includeDelete) {
        try {
            List<OperatorProcess> processes = processManager.getAllCases(includeDelete);
            return Response.ok(processes).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(@PathParam("tenantId") final String tenantId, String query) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String name = JsonUtil.getString(jsonObject, "name");
            String description = JsonUtil.getString(jsonObject, "description");
            Preconditions.checkNotNull(name, I18N.getMessage("pio.error.process.name_can_not_null"));
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"));

            OperatorProcess process = processManager.create(tenantId, name, description);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/template/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createTemplate(@PathParam("tenantId") final String tenantId, String query) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String name = JsonUtil.getString(jsonObject, "name");
            String description = JsonUtil.getString(jsonObject, "description");
            String type = JsonUtil.getString(jsonObject, "type");
            Preconditions.checkNotNull(name, I18N.getMessage("pio.error.process.name_can_not_null"));
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"));
            Preconditions.checkNotNull(type, I18N.getMessage("pio.error.process.template_type_can_not_null"));

            OperatorProcess template = processManager.getTemplate(type);
            if (template != null) {
                throw new RuntimeException(I18N.getMessage("pio.error.process.template_already_exist"));
            }

            OperatorProcess process = processManager.create(tenantId, name, description, type,
                    ProcessConstant.IsTemplate.YES, ProcessConstant.IsCase.NO);

            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/case/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createCase(@PathParam("tenantId") final String tenantId, String query) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String name = JsonUtil.getString(jsonObject, "name");
            String description = JsonUtil.getString(jsonObject, "description");
            Preconditions.checkNotNull(name, I18N.getMessage("pio.error.process.name_can_not_null"));
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"));

            OperatorProcess process = processManager.create(tenantId, name, description, null,
                    ProcessConstant.IsTemplate.NO, ProcessConstant.IsCase.YES);

            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/case/clone/{tenantId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response cloneCase(@PathParam("tenantId") final String tenantId, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String caseId = JsonUtil.getString(jsonObject, "caseId");

            Preconditions.checkNotNull(caseId, I18N.getMessage("pio.error.process.case_id_can_not_null"));
            Preconditions.checkNotNull(tenantId, I18N.getMessage("pio.error.process.tenant_id_can_not_null"));

            OperatorProcess existedCase = processManager.get(tenantId, ProcessConstant.BuiltIn.NO, caseId);
            if (existedCase != null) {
                throw new RuntimeException(I18N.getMessage("pio.error.process.case_already_exist"));
            }

            OperatorProcess process = processManager.cloneCase(tenantId, caseId);

            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/template/spec")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTemplateSpec() {
        try {
            Map<String, String> templateSpec = new HashMap<>();
            templateSpec.put(ProcessConstant.Type.DRAIN_TRAINING, I18N.getMessage("pio.ProcessType.drain_training"));
            templateSpec.put(ProcessConstant.Type.DRAIN_PREDICTION, I18N.getMessage("pio.ProcessType.drain_prediction"));
            return Response.ok(templateSpec).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") final String id, String query) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String name = JsonUtil.getString(jsonObject, "name");
            String description = JsonUtil.getString(jsonObject, "description");
            String status = JsonUtil.getString(jsonObject, "status");

            OperatorProcess process = processManager.update(id, name, description, status);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") final String id) {
        try {
            OperatorProcess process = processManager.delete(id);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") final String id, @QueryParam("all") boolean all) {
        try {
            OperatorProcess process = processManager.getFromCache(id, all);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/run/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response run(@PathParam("id") final String id) {
        try {
            OperatorProcess process = processManager.runAsyn(id);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/result/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getResult(@PathParam("id") final String id) {
        try {
            OperatorProcess process = processManager.getResult(id);
            return Response.ok(process).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
