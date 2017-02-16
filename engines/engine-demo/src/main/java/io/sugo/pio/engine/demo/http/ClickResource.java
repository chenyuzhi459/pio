package io.sugo.pio.engine.demo.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import io.sugo.pio.engine.demo.Click;
import io.sugo.pio.engine.demo.data.MovieItemFeature;
import io.sugo.pio.engine.ocb.Similarity;

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
@Path("query/click")
public class ClickResource {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private static final Map<String, EvictingQueue<Click>> clickMap = new HashMap<>();
    private MovieItemFeature itemFeature = new MovieItemFeature();

    @POST
    @Path("/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submit(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            Click click = jsonMapper.readValue(in, Click.class);
            EvictingQueue<Click> clicks = clickMap.get(click.getUserId());
            if (null == clicks) {
                clicks = EvictingQueue.create(5);
                clickMap.put(click.getUserId(), clicks);
            }

            clicks.add(click);
            return Response.status(Response.Status.ACCEPTED).entity("ok").build();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.ACCEPTED).entity("items not found").build();
    }

    @POST
    @Path("/request")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response request(
            InputStream in,
            @QueryParam("pretty") String pretty,
            @Context final HttpServletRequest req
    ) {
        try {
            String str;
            Similarity similarity = new Similarity();
            HashMap<String,Float> similarityMap = new HashMap<String,Float>();

            ClickQuery query = jsonMapper.readValue(in, ClickQuery.class);
            List<String> items = query.items;
            EvictingQueue<Click> clicks = clickMap.get(query.getUserId());
            List<String> rankItems;
            if (null == clicks) {
                rankItems = items;
            } else {
                for(String itemQ:items) {
                    List<String> featureQ = itemFeature.getItemFeature(itemQ);
                    Double similarityValue = 0.0;
                    for (Click click : clicks) {
                        String itemC = click.getItemId();
                        List<String> featureC = itemFeature.getItemFeature(itemC);
                        Double featureSimilarity =similarity.getSimilarity(featureQ,featureC);
                        similarityValue += featureSimilarity;
                    }
                    similarityMap.put(itemQ,similarityValue.floatValue());
                }
                Map<String,Float> sortSimilarityMap = sortMapByValue(similarityMap);
                rankItems = new ArrayList(sortSimilarityMap.keySet());
            }

            Map<String, List<String>> res = new HashMap<>();
            res.put("item_id", rankItems);
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

    private Map<String, Float> sortMapByValue(Map<String, Float> oriMap) {
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>(){
                public int compare(Map.Entry<String, Float> map1,
                                   Map.Entry<String,Float> map2) {
                    return ((map2.getValue() - map1.getValue() == 0) ? 0
                            : (map2.getValue() - map1.getValue() < 0) ? 1
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

    static class ClickQuery {
        private String userId;
        private List<String> items;

        @JsonCreator
        public ClickQuery(@JsonProperty("userId") String userId, @JsonProperty("items") List<String> items) {
            this.userId = userId;
            this.items = items;
        }

        @JsonProperty
        public String getUserId() {
            return userId;
        }

        @JsonProperty
        public List<String> getItems() {
            return items;
        }
    }
}
