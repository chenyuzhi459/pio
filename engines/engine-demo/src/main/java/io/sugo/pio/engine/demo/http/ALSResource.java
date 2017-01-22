package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.EngineModule;
import io.sugo.pio.engine.als.*;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.demo.ItemUtil;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.popular.LucenceConstants;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.lucene.search.SortField;

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
    private static final String ITEM_NAME = "item_name";
    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/als";

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
            ALSModelFactory alsModelFactory = new ALSModelFactory();
            Repository repository = new LocalFileRepository(REPOSITORY_PATH);

            ALSResult alsResult = alsModelFactory.loadModel(repository).predict(query);
            List<String> itemIds = alsResult.getItems();
            Map<String, List<String>> res = new HashMap<>();
            res.put(Constants.ITEM_ID(), itemIds);
            String str;
            if (!res.isEmpty()) {
                List<String> filmIds = res.get(Constants.ITEM_ID());
                List<String> filmNames = new ArrayList<>(filmIds.size());
                for (String id: filmIds) {
                    filmNames.add(ItemUtil.getTitle(id));
                }
                res.put(ITEM_NAME, filmNames);
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
