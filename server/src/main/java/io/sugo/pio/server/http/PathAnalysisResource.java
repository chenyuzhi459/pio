package io.sugo.pio.server.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.server.http.dto.PathAnalysisDto;
import io.sugo.pio.server.pathanalysis.PathAnalyzer;
import io.sugo.pio.server.pathanalysis.model.AccessTree;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Path("/pio/process/pa/")
public class PathAnalysisResource {

    private static final Logger log = new Logger(PathAnalysisResource.class);

    private final PathAnalyzer pathAnalyzer;

    private final ObjectMapper jsonMapper;

    @Inject
    public PathAnalysisResource(@Json ObjectMapper jsonMapper, PathAnalyzer pathAnalyzer) {
        this.jsonMapper = jsonMapper;
        this.pathAnalyzer = pathAnalyzer;
    }

    @POST
    @Path("/normal")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response normalPath(PathAnalysisDto pathAnalysisDto) {
        check(pathAnalysisDto);
        try {
            String queryStr = pathAnalysisDto.buildQuery();
            AccessTree tree = pathAnalyzer.getAccessTree(queryStr,
                    pathAnalysisDto.getHomePage(), false);

            return Response.ok(tree == null ? Collections.EMPTY_LIST : tree).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/reverse")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response reversePath(PathAnalysisDto pathAnalysisDto) {
        check(pathAnalysisDto);
        try {
            String queryStr = pathAnalysisDto.buildQuery();
            AccessTree tree = pathAnalyzer.getAccessTree(queryStr,
                    pathAnalysisDto.getHomePage(), true);

            return Response.ok(tree == null ? Collections.EMPTY_LIST : tree).build();
        } catch (Throwable e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private void check(PathAnalysisDto pathAnalysisDto) {
        try {
            log.info("Path analysis param: %s", jsonMapper.writeValueAsString(pathAnalysisDto));
        } catch (JsonProcessingException ignore) { }

        Preconditions.checkNotNull(pathAnalysisDto.getDataSource(), "Data source can not be null.");
//        Preconditions.checkNotNull(pathAnalysisDto.getSessionId(), "Session id can not be null.");
        Preconditions.checkNotNull(pathAnalysisDto.getHomePage(), "Home page can not be null.");
        if (pathAnalysisDto.getPages() == null || pathAnalysisDto.getPages().isEmpty()) {
            throw new IllegalArgumentException("Pages can not be empty.");
        }
    }

}
