package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ItemUtil;
import io.sugo.pio.engine.fp.Constants;
import io.sugo.pio.engine.fp.FpQuery;
import io.sugo.pio.engine.fp.LucenceConstants;
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
@Path("query/itemfp")
public class FpResource {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final QueryableModelData modelData;
    private static final String ITEM_NAME = "item_name";

    public static final String REPOSITORY_PATH = "engines/engine-demo/src/main/resources/index/fp";

    public FpResource() throws IOException {
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
            FpQuery query = jsonMapper.readValue(in, FpQuery.class);
            Map<String, Object> map = new LinkedHashMap<>();

            if (query.getItem_id() != null) {
                map.put(Constants.ITEMID(), query.getItem_id());
            }

            int queryNum = 10;
            if (query.getNum() != null) {
                String Num = query.getNum();
                queryNum = Integer.parseInt(Num);
            }

            List<String> resultFields = new ArrayList<>();
            resultFields.add(Constants.CONSEQUENTS());
            Map<String, List<String>> res = modelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.INT, true), queryNum, null);
            Map<String, List<String>> lastRes = new HashMap();
            if (!res.isEmpty()){
                lastRes = GetListItem(res, queryNum);
            }
            String str;
            if (!lastRes.isEmpty()) {
                List<String> filmIds = lastRes.get(Constants.ITEMID());
                List<String> filmNames = new ArrayList<>(filmIds.size());
                for (String id: filmIds) {
                    filmNames.add(ItemUtil.getTitle(id));
                }
                lastRes.put(ITEM_NAME, filmNames);
                str = jsonMapper.writeValueAsString(lastRes);
            } else {
                str = "items not found";
            }
            return Response.status(Response.Status.ACCEPTED).entity(str).build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }

    private Map<String, List<String>> GetListItem(Map<String, List<String>> itemSeqMap, int num){
        Map<String, List<String>> result = new HashMap<>();
        Set<String> itemSet = new HashSet<>();
        List<String> itemSeq = itemSeqMap.get(Constants.CONSEQUENTS());
        for(String items:itemSeq){
            String[] item = items.split(Constants.CONSEQUENT_SEP());
            if (itemSet.size() >= num){
                break;
            }
            for(String itemid: item){
                itemSet.add(itemid);
                if (itemSet.size() >= num){
                    break;
                }
            }
        }
        List<String> itemList = new ArrayList<String>(itemSet);
        result.put(Constants.ITEMID(), itemList);
        return result;
    }

}
