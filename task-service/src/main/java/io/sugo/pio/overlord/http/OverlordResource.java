package io.sugo.pio.overlord.http;


import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.EntryExistsException;
import io.sugo.pio.overlord.TaskMaster;
import io.sugo.pio.overlord.TaskQueue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
@Path("/pio/overlord/")
public class OverlordResource {
    private final TaskMaster taskMaster;

    @Inject
    public OverlordResource(
        TaskMaster taskMaster){
        this.taskMaster = taskMaster;
    }

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {
        return Response.ok("info").build();
    }

    @POST
    @Path("/task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response taskPost(
            final Task task,
            @Context final HttpServletRequest req
    )
    {
        return asLeaderWith(
                taskMaster.getTaskQueue(),
                new Function<TaskQueue, Response>()
                {
                    @Override
                    public Response apply(TaskQueue taskQueue)
                    {
                        try {
                            taskQueue.add(task);
                            return Response.ok(ImmutableMap.of("task", task.getId())).build();
                        }
                        catch (EntryExistsException e) {
                            return Response.status(Response.Status.BAD_REQUEST)
                                    .entity(ImmutableMap.of("error", String.format("Task[%s] already exists!", task.getId())))
                                    .build();
                        }
                    }
                }
        );
    }

    private <T> Response asLeaderWith(Optional<T> x, Function<T, Response> f)
    {
        if (x.isPresent()) {
            return f.apply(x.get());
        } else {
            // Encourage client to try again soon, when we'll likely have a redirect set up
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }
}
