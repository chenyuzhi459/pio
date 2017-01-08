package io.sugo.pio.engine.common.lucene;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public class SearchResult {
    private TopDocs topDocs;

    public SearchResult(TopDocs topDocs) {
        this.topDocs = topDocs;
    }

    public TopDocs getTopDocs() {
        return topDocs;
    }
}
