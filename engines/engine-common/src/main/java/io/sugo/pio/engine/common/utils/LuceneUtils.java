package io.sugo.pio.engine.common.utils;


import io.sugo.pio.engine.common.lucene.NoopAnalyzer;
import io.sugo.pio.engine.common.lucene.SearchResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class LuceneUtils {
    public static IndexWriter getWriter(Directory dir, Analyzer analyzer) {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer;
    }

    public static IndexWriter getWriter(Directory dir) {
        NoopAnalyzer analyzer = new NoopAnalyzer();
        return getWriter(dir, analyzer);
    }

    public static SearchResult search(IndexSearcher searcher, String field, String q, int nums, SortField sortField, Filter filter, Analyzer analyzer) {
        if (analyzer == null){
            analyzer = new NoopAnalyzer();
        }
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = null;

        try {
            query = parser.parse(q);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return search(searcher, nums, query, sortField, filter);
    }

    private static SearchResult search(IndexSearcher searcher, int nums, Query query, SortField sortField, Filter filter) {
        TopDocs topDocs = null;
        try {
            if (sortField != null) {
                topDocs = searcher.search(query, filter, nums, new Sort(sortField));
            } else {
                topDocs = searcher.search(query, nums);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SearchResult result = new SearchResult(topDocs);
        return result;
    }

    public static SearchResult groupSearch(IndexSearcher searcher, String[] fields, String[] qs, int nums, SortField sortField, Filter filter, Analyzer analyzer) {
        if (analyzer == null){
            analyzer = new NoopAnalyzer();
        }
        BooleanClause.Occur[] clauses = new BooleanClause.Occur[]{BooleanClause.Occur.MUST, BooleanClause.Occur.MUST};
        Query query = null;
        try {
            query = MultiFieldQueryParser.parse(qs, fields, clauses, analyzer);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return search(searcher, nums, query, sortField, filter);
    }
}
