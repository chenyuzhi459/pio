package io.sugo.pio.data.druid.internal;

import org.apache.lucene.search.SimpleCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class DocListCollector extends SimpleCollector {
    private List<Integer> docList = new ArrayList<>();

    @Override
    public void collect(int doc) throws IOException {
        docList.add(doc);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    public List<Integer> getDocList() {
        return docList;
    }
}
