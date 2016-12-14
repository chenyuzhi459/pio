package sugo.io.pio.data.druid.internal;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 */
public abstract class StringDimensionSelector implements DimensionSelector<String>{
    private final String dimension;

    public StringDimensionSelector(String dimension) {
        this.dimension = dimension;
    }


    @Override
    public String getDimensionName() {
        return dimension;
    }

}

class StringSingleDimensionSelector extends StringDimensionSelector {
    private SortedDocValues docValues;
    private LuceneCursor cursor;

    public StringSingleDimensionSelector(LuceneCursor cursor, LeafReader leafReader, String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        docValues = leafReader.getSortedDocValues(dimension);
    }


    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public List<String> getValues() {
        if (null != docValues) {
            String val = docValues.get(cursor.getCurrentDoc()).utf8ToString();
            return Arrays.asList(val);
        }
        return Collections.emptyList();
    }

    @Override
    public String getValue() {
        if (null != docValues) {
            String val = docValues.get(cursor.getCurrentDoc()).utf8ToString();
            return val;
        }
        return null;
    }
}

class StringMultiDimensionSelector extends StringDimensionSelector {
    private SortedSetDocValues docValues;
    private LuceneCursor cursor;

    public StringMultiDimensionSelector(LuceneCursor cursor, LeafReader leafReader, String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        docValues = leafReader.getSortedSetDocValues(dimension);
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public List<String> getValues() {
        if (null != docValues) {
            List<String> row = new ArrayList<>();
            docValues.setDocument(cursor.getCurrentDoc());
            long ord;
            while (SortedSetDocValues.NO_MORE_ORDS != (ord = docValues.nextOrd())) {
                row.add(docValues.lookupOrd(ord).utf8ToString());
            }
            return row;
        }
        return Collections.emptyList();
    }

    @Override
    public String getValue() {
        throw new UnsupportedOperationException();
    }
}