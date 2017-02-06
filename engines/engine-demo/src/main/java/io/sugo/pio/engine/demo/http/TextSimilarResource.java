package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.textSimilar.TextSimilarModelFactory;
import io.sugo.pio.engine.textSimilar.TextSimilarQuery;
import io.sugo.pio.engine.textSimilar.TextSimilarResult;
import io.sugo.pio.engine.textSimilar.engine.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Path("query/itemTextSimilar")
public class TextSimilarResource {
    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/textSimilar";

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
            TextSimilarQuery query = jsonMapper.readValue(in, TextSimilarQuery.class);
            TextSimilarModelFactory TextSimilarModelFactory = new TextSimilarModelFactory();
            Repository repository = new LocalFileRepository(REPOSITORY_PATH);
            TextSimilarResult TextSimilarResult = TextSimilarModelFactory.loadModel(repository).predict(query);
            List<String> itemIds = TextSimilarResult.getItems();
            Map<String , List<String>> res = new HashMap<>();
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
