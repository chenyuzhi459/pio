package io.sugo.pio.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.sugo.pio.common.ClusterType;
import io.sugo.pio.common.TaskSubmitter;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.guice.annotations.Json;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Path("/pio/task/")
public class TaskResource {
    private final ObjectMapper jsonMapper;
    private final TaskSubmitter taskSubmitter;


    @Inject
    public TaskResource(@Json ObjectMapper jsonMapper
            ,TaskSubmitter taskSubmitter
    ) {
        this.jsonMapper = jsonMapper;
        this.taskSubmitter = taskSubmitter;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {

        String appId = null;
        try {
            Task task = jsonMapper.readValue(in, Task.class);
            appId = taskSubmitter.submit(ClusterType.YARN,task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity(appId).build();
    }
}