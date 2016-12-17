package sugo.io.pio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import sugo.io.pio.guice.annotations.Json;
import sugo.io.pio.metadata.SparkConfig;
import sugo.io.pio.task.ClusterType;
import sugo.io.pio.task.Task;
import sugo.io.pio.task.TaskSubmitter;

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
    private final EngineStorage engineStorage;
    private final SparkConfig sparkConfig;

    @Inject
    public TaskResource(@Json ObjectMapper jsonMapper
            , EngineStorage engineStorage
            , SparkConfig sparkConfig
    ) {
        this.jsonMapper = jsonMapper;
        this.engineStorage = engineStorage;
        this.sparkConfig = sparkConfig;

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServer()
    {
        return Response.status(Response.Status.ACCEPTED).entity("Hello world").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        //http://localhost:8080/pio/task?pretty=YARN
        //{"type":"training","id":"555","url":"ttt"}

        final String reqContentType = req.getContentType();
        final ObjectWriter jsonWriter = pretty != null
                ? jsonMapper.writerWithDefaultPrettyPrinter()
                : jsonMapper.writer();

        System.out.println(engineStorage.toString());
        String appId = null;
        try {
            Task task = jsonMapper.readValue(in, Task.class);
            appId = TaskSubmitter.submit(ClusterType.YARN,task,engineStorage, sparkConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity(appId).build();
    }
}