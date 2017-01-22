package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.demo.ItemUtil;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.detail.*;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;

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
    private static final String RELATED_ITEM_NAME = "related_item_name";

    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/detail";

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
            DetailModelFactory detailModelFactory = new DetailModelFactory();
            Repository repository = new LocalFileRepository(REPOSITORY_PATH);

            DetailResult alsResult = detailModelFactory.loadModel(repository).predict(query);
            List<String> itemIds = alsResult.getItems();
            Map<String, List<String>> res = new HashMap<>();
            res.put(Constants.RELATED_ITEM_ID(), itemIds);

            String str;
            if (!res.isEmpty()) {
                List<String> filmIds = res.get(Constants.RELATED_ITEM_ID());
                List<String> filmNames = new ArrayList<>(filmIds.size());
                for (String id: filmIds) {
                    filmNames.add(ItemUtil.getTitle(id));
                }
                res.put(RELATED_ITEM_NAME, filmNames);
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
