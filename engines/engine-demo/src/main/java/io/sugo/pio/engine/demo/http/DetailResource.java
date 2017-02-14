package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.als.ALSModelFactory;
import io.sugo.pio.engine.als.ALSResult;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.detail.*;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.PredictionModel;

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
@Path("query/detail")
public class DetailResource {
    public static final String REPOSITORY_PATH = "repositories/detail";

    private static PredictionModel<DetailResult> model;

    static {
        Repository repository = new LocalFileRepository(REPOSITORY_PATH);
        DetailModelFactory detailModelFactory = new DetailModelFactory(repository);
        model = detailModelFactory.loadModel();
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
            DetailQuery query = jsonMapper.readValue(in, DetailQuery.class);
            DetailResult alsResult = model.predict(query);
            List<String> itemIds = alsResult.getItems();
            Map<String, List<String>> res = new HashMap<>();
            res.put(Constants.RELATED_ITEM_ID(), itemIds);

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
