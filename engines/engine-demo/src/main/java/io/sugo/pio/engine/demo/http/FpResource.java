package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.detail.DetailModelFactory;
import io.sugo.pio.engine.detail.DetailResult;
import io.sugo.pio.engine.fp.*;
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
@Path("query/itemfp")
public class FpResource {
    public static final String REPOSITORY_PATH = "repositories/fp";

    private static PredictionModel<FpResult> model;

    static {
        Repository repository = new LocalFileRepository(REPOSITORY_PATH);
        FpModelFactory fpModelFactory = new FpModelFactory(repository);
        model = fpModelFactory.loadModel();
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
            FpQuery fpQuery = jsonMapper.readValue(in, FpQuery.class);
            FpResult searchResult = model.predict(fpQuery);
            List<String> itemIds = searchResult.getItems();

            int queryNum = 10;
            Map<String, List<String>> lastRes = new HashMap();
            if (itemIds != null){
                lastRes = GetListItem(itemIds, queryNum);
            }
            else {
                lastRes = null;
            }
            String str;
            if (lastRes != null) {
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

    private Map<String, List<String>> GetListItem(List<String> itemSeqMap, int num){
        Map<String, List<String>> result = new HashMap<>();
        Set<String> itemSet = new HashSet<>();
        List<String> itemSeq = itemSeqMap;
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
