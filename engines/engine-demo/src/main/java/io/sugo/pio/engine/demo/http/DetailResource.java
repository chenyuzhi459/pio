package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.demo.ItemUtil;
import io.sugo.pio.engine.detail.Constants;
import io.sugo.pio.engine.detail.DetailQuery;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Path("query/detail")
public class DetailResource {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final QueryableModelData modelData;
    private static final String RELATED_ITEM_NAME = "related_item_name";

    public static final String REPOSITORY_PATH = "src/main/resources/index/detail";

    public DetailResource() throws IOException {
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
        try {
            DetailQuery query = jsonMapper.readValue(in, DetailQuery.class);
            Map<String, Object> map = new LinkedHashMap<>();

            if (query.getItemId() != null) {
                map.put(Constants.ITEM_ID(), query.getItemId());
            }

            int queryNum = 10;
            if (query.getNum() != null) {
                String Num = query.getNum();
                queryNum = Integer.parseInt(Num);
            }

            List<String> resultFields = new ArrayList<>();
            resultFields.add(Constants.RELATED_ITEM_ID());
            Map<String, List<String>> res = modelData.predict(map, resultFields, null, queryNum);
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