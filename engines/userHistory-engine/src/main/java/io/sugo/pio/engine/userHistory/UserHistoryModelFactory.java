package io.sugo.pio.engine.userHistory;

import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.LocalFileRepository;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import org.ansj.lucene5.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.*;

/**
 */
public class UserHistoryModelFactory implements ModelFactory<UserHistoryResult> {
    @Override
    public PredictionModel<UserHistoryResult> loadModel(Repository repository) {
        try{
            return new UserHistoryPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    static class UserHistoryPredictionModel implements PredictionModel<UserHistoryResult>{
        public static final String SEARCH_PATH = "engines/engine-demo/src/main/resources/index/search";
        public static final String DETAIL_PATH = "engines/engine-demo/src/main/resources/index/detail";
        private final QueryableModelData queryableModelData;
        UserHistoryPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public UserHistoryResult predict(PredictionQueryObject query) {
            try {
                UserHistoryQuery UserHistoryQuery = (UserHistoryQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (UserHistoryQuery.getUser_id() != null) {
                    map.put(Constants.USER_ID(), UserHistoryQuery.getUser_id());
                }

                int queryNum = 10;
                if (UserHistoryQuery.getNum() != null) {
                    String Num = UserHistoryQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.ITEM_ID());
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, new SortField(LucenceConstants.SCORE(), SortField.Type.LONG, true), 30,null);
                List<String> hisItemIds = res.get(Constants.ITEM_ID());

                String searchWord = UserHistoryQuery.getItem_name();
                List<String> seaItemIds = GetSearchItemId(searchWord);
                List<String> lastItemIds = GetSimilarItems(hisItemIds, seaItemIds, queryNum);

                return new UserHistoryResult(lastItemIds);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<String> GetSearchItemId(String itemName) throws IOException{
            int queryNum = 90;
            Repository repository = new LocalFileRepository(SEARCH_PATH);
            QueryableModelData searchModel = new QueryableModelData(repository);
            Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(io.sugo.pio.engine.search.Constants.ITEM_NAME(), itemName);
            List<String> resultFields = new ArrayList<>();
            resultFields.add(io.sugo.pio.engine.search.Constants.ITEM_ID());
            Map<String, List<String>> res = searchModel.predict(map, resultFields, null, queryNum,analyzer);
            List<String> searchIds = res.get(io.sugo.pio.engine.search.Constants.ITEM_ID());
            return searchIds;
        }

        private List<String> GetSimilarItems(List<String> hisIds, List<String> seaIds, int num) throws IOException{
            Repository repository = new LocalFileRepository(DETAIL_PATH);
            QueryableModelData simModel = new QueryableModelData(repository);
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
                    Map<String, List<String>> res = simModel.predict(map, resultFields, null, queryNum, null);
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
}
