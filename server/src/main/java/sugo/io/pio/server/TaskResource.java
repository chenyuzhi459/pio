package sugo.io.pio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.guice.annotations.Json;
import sugo.io.pio.metadata.MetadataStorageConnectorConfig;
import sugo.io.pio.metadata.MetadataStorageTablesConfig;
import sugo.io.pio.metadata.SparkServerConfig;
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
    private final SparkServerConfig sparkServerConfig;

    @Inject
    public TaskResource(@Json ObjectMapper jsonMapper,
                        SparkServerConfig sparkServerConfig) {
        this.jsonMapper = jsonMapper;
        this.sparkServerConfig = sparkServerConfig;
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
        final String reqContentType = req.getContentType();
        final ObjectWriter jsonWriter = pretty != null
                ? jsonMapper.writerWithDefaultPrettyPrinter()
                : jsonMapper.writer();

        try {
            Task task = jsonMapper.readValue(in, Task.class);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return Response.status(Response.Status.ACCEPTED).entity("Hello world").build();
    }
}
