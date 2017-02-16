package io.sugo.pio.recommend.manage.http;


import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.recommend.AlgorithmManager;
import io.sugo.pio.recommend.RecommendManager;
import io.sugo.pio.recommend.bean.RecInstance;
import io.sugo.pio.recommend.bean.RecInstanceCriteria;
import io.sugo.pio.recommend.bean.RecStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/pio/manage/recommend")
public class RecManageResource {
    private static final Logger log = new Logger(RecManageResource.class);
    private final RecommendManager recommendManager;

    @Inject
    public RecManageResource(
            RecommendManager recommendManager
    ) {
        this.recommendManager = recommendManager;
    }

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo(
            @Context final HttpServletRequest req
    ) {
        String ret = String.format("recommend info, id:%s Session:%s RequestedSessionId:%s RemoteAddr:%s Host:%s port:%s",
                this.toString(),
                req.getSession().getId(),
                req.getRequestedSessionId(),
                req.getRemoteAddr(),
                req.getRemoteHost(),
                req.getRemotePort()
        );
        return Response.ok(ret).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") final String id) {
        try {
            RecInstance recInstance = recommendManager.getRecInstance(id);
            if (recInstance == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no data found with id:" + id).build();
            }
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll(RecInstanceCriteria criteria) {
        try {
            criteria.check();
            List<RecInstance> recInstance = recommendManager.getAll(criteria);
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/create")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(RecInstance recInstance) {
        try {
            Preconditions.checkNotNull(recInstance.getId(), "Must specify id");
            Preconditions.checkNotNull(recInstance.getName(), "Must specify name");
            Preconditions.checkNotNull(recInstance.getNum(), "Must specify num");
            recommendManager.create(recInstance);
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/update/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") final String id, RecInstance recInstance) {
        try {
            recInstance.setId(id);
            recInstance = recommendManager.update(recInstance);
            if (recInstance == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no data found with id:" + id).build();
            }
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/enable/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response enable(@PathParam("id") final String id, @QueryParam("enabled") Boolean enabled) {
        try {
            if (enabled == null) {
                enabled = false;
            }
            RecInstance recInstance = recommendManager.enable(id, enabled);
            if (recInstance == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no data found with id:" + id).build();
            }
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") final String id) {
        try {
            RecInstance recInstance = recommendManager.delete(id);
            if (recInstance == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no data found with id:" + id).build();
            }
            return Response.ok(recInstance).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/addStrategy/{recId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addStrategy(@PathParam("recId") final String recId, RecStrategy recStrategy) {
        try {
            Preconditions.checkNotNull(recStrategy.getId(), "Must specify id");
            Preconditions.checkNotNull(recStrategy.getName(), "Must specify name");
            Preconditions.checkArgument(recStrategy.getTypes() != null && !recStrategy.getTypes().isEmpty(), "Must specify types");
            Preconditions.checkNotNull(recStrategy.getOrderField(), "Must specify orderField");
            recommendManager.addStrategy(recId, recStrategy);
            return Response.ok(recStrategy).build();
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/updateStrategy/{recId}/{strategyId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateStrategy(@PathParam("recId") final String recId, @PathParam("strategyId") final String strategyId, RecStrategy recStrategy) {
        try {
//            Preconditions.checkNotNull(recStrategy.getName(), "name cannot be null");
            recommendManager.updateStrategy(recId, strategyId, recStrategy);
            return Response.ok(recStrategy).build();
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/adjustPercent/{recId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response adjustPercent(@PathParam("recId") final String recId, Map<String, Integer> percents) {
        try {
            RecInstance entry = recommendManager.adjustPercent(recId, percents);
            return Response.ok(entry).build();
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/deleteStrategy/{recId}/{strategyId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response deleteStrategy(@PathParam("recId") final String recId, @PathParam("strategyId") final String strategyId) {
        try {
            RecStrategy recStrategy = recommendManager.deleteStrategy(recId, strategyId);
            if (recStrategy == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("no data found with id:" + recId + "/" + strategyId).build();
            }
            return Response.ok(recStrategy).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/algorithms")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllAlgorithms() {
        try {
            return Response.ok(AlgorithmManager.getAll()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
