package io.sugo.pio.engine.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.common.data.QueryableModelData;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import org.ansj.lucene5.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.SortField;
import scala.collection.immutable.Stream;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class SearchModelFactory implements ModelFactory<SearchResult> {
    private final Repository repository;

    @JsonCreator
    public SearchModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<SearchResult> loadModel() {
        try{
            return new SearchPredictionModel(new QueryableModelData(repository));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class SearchPredictionModel implements PredictionModel<SearchResult>{
        private final QueryableModelData queryableModelData;
        SearchPredictionModel(QueryableModelData queryableModelData){
            this.queryableModelData = queryableModelData;
        }

        @Override
        public SearchResult predict(PredictionQueryObject query) {
            try {
                SearchQuery SearchQuery = (SearchQuery) query;
                Map<String, Object> map = new LinkedHashMap<>();

                if (SearchQuery.getItem_name() != null) {
                    map.put(Constants.ITEM_NAME(), SearchQuery.getItem_name());
                }

                int queryNum = 10;
                if (SearchQuery.getNum() != null) {
                    String Num = SearchQuery.getNum();
                    queryNum = Integer.parseInt(Num);
                }

                List<String> resultFields = new ArrayList<>();
                resultFields.add(Constants.ITEM_NAME());
                resultFields.add(Constants.ITEM_ID());
                Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj);
                Map<String, List<String>> res = queryableModelData.predict(map, resultFields, null, queryNum,analyzer);
                List<String> itemnames = res.get(Constants.ITEM_NAME());
                List<String> itemids = res.get(Constants.ITEM_ID());

                return new SearchResult(itemids, itemnames);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
