package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.articleClu.ArtiClusterModelFactory;
import io.sugo.pio.engine.articleClu.ArtiClusterQuery;
import io.sugo.pio.engine.articleClu.ArtiClusterResult;
import io.sugo.pio.engine.articleClu.engine.Constants;
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
@Path("query/artclu")
public class ArtiClusterResource {
    public static final String CLF_PATH = "repositories/artclu/clf";
    public static final String W2V_PATH  = "repositories/artclu/w2v";
    public static final String MAP_PATH   = "repositories/artclu/map";
    private static PredictionModel<ArtiClusterResult> model;

    static {
        Repository clf_repository = new LocalFileRepository(CLF_PATH);
        Repository w2v_repository = new LocalFileRepository(W2V_PATH);
        Repository map_repository = new LocalFileRepository(MAP_PATH);
        ArtiClusterModelFactory artiClusterModelFactory = new ArtiClusterModelFactory(clf_repository, w2v_repository, map_repository);
        model = artiClusterModelFactory.loadModel();
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
            ArtiClusterQuery query = jsonMapper.readValue(in, ArtiClusterQuery.class);
            ArtiClusterResult artiClusterResult = model.predict(query);
            if (artiClusterResult != null)
            {
                String label = artiClusterResult.getLabel();
                Map<String, String> res = new HashMap<>();
                res.put(Constants.LABEL_RESULT(), label);
                String str;
                if (!res.isEmpty()) {
                    str = jsonMapper.writeValueAsString(res);
                } else {
                    str = "items not found";
                }
                return Response.status(Response.Status.ACCEPTED).entity(str).build();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }
}
