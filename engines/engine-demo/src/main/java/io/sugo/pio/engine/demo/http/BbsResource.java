package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.bbs.BbsModelFactory;
import io.sugo.pio.engine.bbs.BbsQuery;
import io.sugo.pio.engine.bbs.BbsResult;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.prediction.PredictionModel;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Path("query/bbs")
public class BbsResource {
    public static final String PATH = "repositories/bbs";
    private static PredictionModel<BbsResult> model;
    static {
        Repository repository = new LocalFileRepository(PATH);
        BbsModelFactory bbsModelFactory = new BbsModelFactory(repository);
        model = bbsModelFactory.loadModel();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doPost(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            ObjectMapper jsonMapper = ObjectMapperUtil.getObjectMapper();
            BbsQuery query = jsonMapper.readValue(in, BbsQuery.class);
            BbsResult bbsResult = model.predict(query);
            String[] items = bbsResult.getItems();
            String[] articles = bbsResult.getArticles();
            Map<String, String[]> res = new HashMap<>();
            res.put("items", items);
            res.put("articles", articles);
            String str;
            if (!res.isEmpty()) {
                str = jsonMapper.writeValueAsString(res);
            } else {
                str = "items not found";
            }
            return Response.status(Response.Status.ACCEPTED).entity(str).build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }
}
