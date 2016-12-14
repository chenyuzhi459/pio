package sugo.io.pio.data.druid.internal;

import com.google.common.primitives.Ints;
import org.apache.lucene.index.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 */
public abstract class FloatDimensionSelector implements DimensionSelector<Float> {
    private final String dimension;

    public FloatDimensionSelector(String dimension) {
        this.dimension = dimension;
    }


    @Override
    public String getDimensionName() {
        return dimension;
    }
}

class FloatSingleDimensionSelector extends FloatDimensionSelector {
    private final LuceneCursor cursor;
    private final NumericDocValues docValues;
    private final String dimension;

    public FloatSingleDimensionSelector(
        LuceneCursor cursor,
        LeafReader leafReader,
        String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        this.dimension = dimension;
        docValues = leafReader.getNumericDocValues(dimension);
    }

    @Override
    public String getDimensionName() {
        return dimension;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public List<Float> getValues() {
        if(null != docValues) {
            long value = docValues.get(cursor.getCurrentDoc());
            float fValue = Float.intBitsToFloat(Ints.checkedCast(value));
            return Arrays.asList(fValue);
        }
        return Collections.emptyList();
    }

    @Override
    public Float getValue() {
        if(null != docValues) {
            long value = docValues.get(cursor.getCurrentDoc());
            return Float.intBitsToFloat(Ints.checkedCast(value));
        }
        return null;
    }
}

class FloatMultiDimensionSelector extends FloatDimensionSelector {
    private final LuceneCursor cursor;
    private SortedNumericDocValues docValues;
    private final String dimension;

    public FloatMultiDimensionSelector(
            LuceneCursor cursor,
            LeafReader leafReader,
            String dimension) throws IOException {
        super(dimension);
        this.cursor = cursor;
        this.dimension = dimension;
        docValues = leafReader.getSortedNumericDocValues(dimension);
    }

    @Override
    public String getDimensionName() {
        return dimension;
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public List<Float> getValues() {
        if (null != docValues) {
            docValues.setDocument(cursor.getCurrentDoc());
            List<Float> row = new ArrayList<>(docValues.count());
            for (int i=0;i<row.size();i++) {
                row.set(i, Float.intBitsToFloat(Ints.checkedCast(docValues.valueAt(i))));
            }
            return row;
        }
        return Collections.emptyList();
    }

    @Override
    public Float getValue() {
        throw new UnsupportedOperationException();
    }
}