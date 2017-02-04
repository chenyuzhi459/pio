package io.sugo.pio.data.druid.internal;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedNumericDocValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 */
public abstract class LongDimensionSelector implements DimensionSelector<Long> {
    private final String dimension;

    public LongDimensionSelector(String dimension) {
        this.dimension = dimension;
    }


    @Override
    public String getDimensionName() {
        return dimension;
    }
}

class LongSingleDimensionSelector extends LongDimensionSelector {
    private final LuceneCursor cursor;
    private final NumericDocValues docValues;


    public LongSingleDimensionSelector(
            LuceneCursor cursor,
            LeafReader leafReader,
            String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        docValues = leafReader.getNumericDocValues(dimension);
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public List<Long> getValues() {
        if (null != docValues) {
            long value = docValues.get(cursor.getCurrentDoc());
            return Arrays.asList(value);
        }
        return Collections.emptyList();
    }

    @Override
    public Long getValue() {
        if (null != docValues) {
            return docValues.get(cursor.getCurrentDoc());
        }
        return null;
    }
}

class LongMultiDimensionSelector extends LongDimensionSelector {
    private final LuceneCursor cursor;
    private final SortedNumericDocValues docValues;

    public LongMultiDimensionSelector(
            LuceneCursor cursor,
            LeafReader leafReader,
            String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        docValues = leafReader.getSortedNumericDocValues(dimension);
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public List<Long> getValues() {
        if (null != docValues) {
            List<Long> row = new ArrayList<>(docValues.count());
            docValues.setDocument(cursor.getCurrentDoc());
            for (int i = 0; i < row.size(); i++) {
                row.set(i, docValues.valueAt(i));
            }
            return row;
        }
        return Collections.emptyList();
    }

    @Override
    public Long getValue() {
        throw new UnsupportedOperationException();
    }
}