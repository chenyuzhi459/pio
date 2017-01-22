package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.search.Constants;
import io.sugo.pio.engine.search.SearchQuery;
import org.ansj.lucene5.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 */
@Path("query/userHistory")
public class UserHistoryResource {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final QueryableModelData modelData;

    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/userhistory";

    public UserHistoryResource() throws IOException {
        Repository repository = new LocalFileRepository(REPOSITORY_PATH);
        modelData = new QueryableModelData(repository);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }
}
