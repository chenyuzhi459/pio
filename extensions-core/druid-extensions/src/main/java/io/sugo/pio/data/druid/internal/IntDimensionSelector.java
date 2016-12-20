package io.sugo.pio.data.druid.internal;

import com.google.common.primitives.Ints;
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
public abstract class IntDimensionSelector implements DimensionSelector<Integer>{
    private final String dimension;

    public IntDimensionSelector(String dimension) {
        this.dimension = dimension;
    }


    @Override
    public String getDimensionName() {
        return dimension;
    }

}

class IntSingleDimensionSelector extends IntDimensionSelector {
    private final LuceneCursor cursor;
    private final NumericDocValues docValues;

    public IntSingleDimensionSelector(
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
    public List<Integer> getValues() {
        if(null != docValues) {
            long value = docValues.get(cursor.getCurrentDoc());
            return Arrays.asList(Ints.checkedCast(value));
        }
        return Collections.emptyList();
    }

    @Override
    public Integer getValue() {
        if(null != docValues) {
            long value = docValues.get(cursor.getCurrentDoc());
            return Ints.checkedCast(value);
        }
        return null;
    }
}

class IntMultiDimensionSelector extends IntDimensionSelector {
    private final LuceneCursor cursor;
    private final SortedNumericDocValues docValues;

    public IntMultiDimensionSelector(
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
    public List<Integer> getValues() {
        if (null != docValues) {
            List<Integer> row = new ArrayList<>(docValues.count());
            docValues.setDocument(cursor.getCurrentDoc());
            for (int i=0;i<row.size();i++) {
                row.set(i, Ints.checkedCast(docValues.valueAt(i)));
            }
            return row;
        }
        return Collections.emptyList();
    }

    @Override
    public Integer getValue() {
        throw new UnsupportedOperationException();
    }
}