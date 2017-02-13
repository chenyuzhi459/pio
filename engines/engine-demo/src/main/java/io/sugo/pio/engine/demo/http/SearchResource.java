package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.search.Constants;
import io.sugo.pio.engine.search.SearchModelFactory;
import io.sugo.pio.engine.search.SearchQuery;
import io.sugo.pio.engine.search.SearchResult;

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
@Path("query/itemSearch")
public class SearchResource {
    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/search";

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
            SearchQuery query = jsonMapper.readValue(in, SearchQuery.class);
            Repository repository = new LocalFileRepository(REPOSITORY_PATH);
            SearchModelFactory searchModelFactory = new SearchModelFactory(repository);
            SearchResult searchResult = searchModelFactory.loadModel().predict(query);
            List<String> itemIds = searchResult.getItems();
            List<String> itemNames = searchResult.getNames();
            Map<String , List<String>> res = new HashMap<>();
            res.put(Constants.ITEM_ID(), itemIds);
            res.put(Constants.ITEM_NAME(), itemNames);

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
