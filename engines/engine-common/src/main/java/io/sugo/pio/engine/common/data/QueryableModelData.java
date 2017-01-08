package io.sugo.pio.engine.common.data;

import io.sugo.pio.engine.common.lucene.RepositoryDirectory;
import io.sugo.pio.engine.common.lucene.SearchResult;
import io.sugo.pio.engine.common.utils.LuceneUtils;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.commons.collections.map.HashedMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class QueryableModelData {
    private final IndexSearcher indexSearcher;

    public QueryableModelData(Repository repository) throws IOException {
        IndexReader reader = DirectoryReader.open(new RepositoryDirectory(repository));
        indexSearcher = new IndexSearcher(reader);
    }

    /**
     * @param params
     * @return
     * @throws IOException
     */
    public Map<String, List<String>> predict(Map<String, Object> params, List<String> resultFields, SortField sortField, int queryNum) throws IOException {
        String[] fields = new String[params.size()];
        params.keySet().toArray(fields);
        String[] qs = new String[params.size()];
        params.values().toArray(qs);
        SearchResult result;
        if (params.size() == 1) {
            result = LuceneUtils.search(indexSearcher, fields[0], qs[0], queryNum, sortField, null);
        } else {
            result = LuceneUtils.groupSearch(indexSearcher, fields, qs, queryNum, sortField, null);
        }

        TopDocs topDocs = result.getTopDocs();
        Map<String, List<String>> res = new HashedMap();
        if (topDocs.totalHits == 0) {
            return res;
        }
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(sd.doc);
            for (String field : resultFields) {
                if (!res.containsKey(field)) {
                    List<String> resList = new ArrayList<>();
                    resList.add(doc.get(field));
                    res.put(field, resList);
                } else {
                    List<String> resList = res.get(field);
                    resList.add(doc.get(field));
                    res.put(field, resList);
                }
            }
        }
        return res;
    }


}
