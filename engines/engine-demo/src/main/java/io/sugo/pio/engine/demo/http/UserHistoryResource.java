package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.ObjectMapperUtil;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.search.SearchModelFactory;
import io.sugo.pio.engine.search.SearchResult;
import io.sugo.pio.engine.userHistory.Constants;
import io.sugo.pio.engine.userHistory.UserHistoryModelFactory;
import io.sugo.pio.engine.userHistory.UserHistoryQuery;
import io.sugo.pio.engine.userHistory.UserHistoryResult;
import org.apache.lucene.analysis.Analyzer;
import org.ansj.lucene5.AnsjAnalyzer;

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
@Path("query/userSearch")
public class UserHistoryResource {
    public static final String REPOSITORY_PATH = "repositories/userhistory";
    public static final String SEARCH_PATH = "repositories/search";
    public static final String DETAIL_PATH = "repositories/detail";
    private final QueryableModelData seaModelData;
    private final QueryableModelData detailModelData;

    private static PredictionModel<UserHistoryResult> model;

    static {
        Repository repository = new LocalFileRepository(REPOSITORY_PATH);
        UserHistoryModelFactory userHistoryModelFactory = new UserHistoryModelFactory(repository);
        model = userHistoryModelFactory.loadModel();
    }

    public UserHistoryResource() throws IOException {
        Repository seaRepository = new LocalFileRepository(SEARCH_PATH);
        Repository detRository = new LocalFileRepository(DETAIL_PATH);
        seaModelData = new QueryableModelData(seaRepository);
        detailModelData = new QueryableModelData(detRository);
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
            UserHistoryResult userHistoryResult = model.predict(userHistoryQuery);
            List<String> hisItemIds = userHistoryResult.getItems();
            Map<String , List<String>> res = new HashMap<>();

            List<String> lastItemIds = null;
            int queryNum = 10;
            if (userHistoryQuery.getNum() != null) {
                String Num = userHistoryQuery.getNum();
                queryNum = Integer.parseInt(Num);
            }
            String searchWord = userHistoryQuery.getItem_name();
                List<String> seaItemIds = GetSearchItemId(searchWord);
                if (hisItemIds != null && seaItemIds!= null){
                    lastItemIds = GetSimilarItems(hisItemIds, seaItemIds, queryNum);
                }
            res.put(Constants.ITEM_ID(), lastItemIds);
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

    private List<String> GetSearchItemId(String itemName) throws IOException{
        int queryNum = 90;
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(io.sugo.pio.engine.search.Constants.ITEM_NAME(), itemName);
        List<String> resultFields = new ArrayList<>();
        resultFields.add(io.sugo.pio.engine.search.Constants.ITEM_ID());
        Map<String, List<String>> res = seaModelData.predict(map, resultFields, null, queryNum,analyzer);
        List<String> searchIds = null;
        if (res != null){
            searchIds = res.get(io.sugo.pio.engine.search.Constants.ITEM_ID());
        }
        return searchIds;
    }

    private List<String> GetSimilarItems(List<String> hisIds, List<String> seaIds, int num) throws IOException{
        Map<String, Float> scoreMap = new HashMap<String, Float>();
        Map<String, Float> sortMap = new HashMap<String, Float>();
        ArrayList<String> resItems = new ArrayList<String>();

        for(String seaId: seaIds){
            float sumSim = 0;
            int numSimItem = 0;
            for(String hisId: hisIds){
                float sim = 0;
                int queryNum = 1;
                Map<String, Object> map = new LinkedHashMap<>();
                map.put(io.sugo.pio.engine.detail.Constants.ITEM_ID(),seaId);
                map.put(io.sugo.pio.engine.detail.Constants.RELATED_ITEM_ID(), hisId);
                List<String> resultFields = new ArrayList<>();
                resultFields.add(io.sugo.pio.engine.detail.LucenceConstants.SCORE());
                Map<String, List<String>> res = detailModelData.predict(map, resultFields, null, queryNum, null);
                if (!res.isEmpty()){
                    sim = Float.parseFloat(res.get(io.sugo.pio.engine.detail.LucenceConstants.SCORE()).get(0));
                    numSimItem = numSimItem + 1;
                }
                sumSim = sumSim + sim;
            }
            if( 0 != numSimItem){
                scoreMap.put(seaId, (sumSim/numSimItem) );
            }
        }
        sortMap = sortMapByValue(scoreMap);
        Set<String> itemset = sortMap.keySet();
        Iterator<String> it = itemset.iterator();
        while ( it.hasNext() && resItems.size()<num ) {
            String str = it.next();
            resItems.add(str);
        }
        return resItems;
    }

    private Map<String, Float> sortMapByValue(Map<String, Float> oriMap) {
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>(){
                public int compare(Map.Entry<String, Float> map1,
                                   Map.Entry<String,Float> map2) {
                    return ((map2.getValue() - map1.getValue() == 0) ? 0
                            : (map2.getValue() - map1.getValue() > 0) ? 1
                            : -1);
                }
            });
            Iterator<Map.Entry<String, Float>> iter = entryList.iterator();
            Map.Entry<String, Float> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }
}
