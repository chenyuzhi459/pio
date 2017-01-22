package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ItemUtil;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.userHistory.Constants;
import io.sugo.pio.engine.userHistory.UserHistoryModelFactory;
import io.sugo.pio.engine.userHistory.UserHistoryQuery;
import io.sugo.pio.engine.userHistory.UserHistoryResult;

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
    private final String ITEM_NAME = "item_name";
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final QueryableModelData modelData;

    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/userhistory";
    public static final String SEARCH_PATH = "engines/engine-demo/src/main/resources/index/search";
    public static final String DETAIL_PATH = "engines/engine-demo/src/main/resources/index/detail";

    public UserHistoryResource() throws IOException {
        Repository repository = new LocalFileRepository(DETAIL_PATH);
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
        try {
            ObjectMapper jsonMapper = ObjectMapperUtil.getObjectMapper();
            UserHistoryQuery userHistoryQuery = jsonMapper.readValue(in, UserHistoryQuery.class);
            UserHistoryModelFactory userHistoryModelFactory = new UserHistoryModelFactory();
            Repository repository = new LocalFileRepository(REPOSITORY_PATH);

            UserHistoryResult userHistoryResult = userHistoryModelFactory.loadModel(repository).predict(userHistoryQuery);
            List<String> itemIds = userHistoryResult.getItems();
            Map<String , List<String>> res = new HashMap<>();
            if(!res.isEmpty()){
                res.put(Constants.ITEM_ID(), itemIds);
            }
            String str;
            if (!res.isEmpty()) {
                List<String> filmIds = itemIds;
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
