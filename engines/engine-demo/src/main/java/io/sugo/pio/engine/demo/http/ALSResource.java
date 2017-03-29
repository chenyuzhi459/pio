package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.als.*;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
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
@Path("query/als")
public class ALSResource {
    public static final String REPOSITORY_PATH = "repositories/als";

    private static PredictionModel<ALSResult> model;

    static {
        Repository repository = new LocalFileRepository(REPOSITORY_PATH);
        ALSModelFactory alsModelFactory = new ALSModelFactory(repository);
        model = alsModelFactory.loadModel();
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
            ALSQuery query = jsonMapper.readValue(in, ALSQuery.class);
            ALSResult alsResult = model.predict(query);
            List<String> itemIds = alsResult.getItems();
            Map<String, List<String>> res = new HashMap<>();
            res.put(Constants.ITEM_ID(), itemIds);
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
