package io.sugo.pio.server.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.server.process.ProcessManager;
import io.sugo.pio.server.utils.JsonUtil;
import org.codehaus.jettison.json.JSONException;
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
    public Response getAll() {
        List<OperatorProcess> processes = processManager.getAll();
        return Response.ok(processes).build();
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

            OperatorProcess process = processManager.create(name, description);
            return Response.ok(process).build();
        } catch (JSONException e) {
            return Response.serverError().build();
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

            OperatorProcess process = processManager.update(id, name, description);
            return Response.ok(process).build();
        } catch (JSONException e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") final String id) {
        OperatorProcess process = processManager.delete(id);
        return Response.ok(process).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") final String id) {
        OperatorProcess process = processManager.get(id);
        return Response.ok(process).build();
    }

}
