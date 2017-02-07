package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.utils.JsonUtil;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(@QueryParam("all") boolean all) {
        try {
            List<OperatorProcess> processes = processManager.getAll(all);
            return Response.ok(processes).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(String query) {
        try {
            JSONObject jsonObject = new JSONObject(query);

            String name = JsonUtil.getString(jsonObject, "name");
            String description = JsonUtil.getString(jsonObject, "description");
            Preconditions.checkNotNull(name, "process name cannot be null");

            OperatorProcess process = processManager.create(name, description);
            return Response.ok(process).build();
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") final String id, @QueryParam("all") boolean all) {
        try {
            OperatorProcess process = processManager.get(id, all);
            return Response.ok(process).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/run/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response run(@PathParam("id") final String id) {
        try {
            OperatorProcess process = processManager.run(id);
            return Response.ok(process).build();
        } catch (Exception e) {
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
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
