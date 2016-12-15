package sugo.io.pio.data.druid.internal;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;
import java.util.List;

/**
 */
public class LuceneCursor {
    private final FieldMappings fieldMappings;
    private final LeafReader leafReader;

    private final List<Integer> docList;
    private final int maxDocOffset;
    private boolean done;
    private int offset = 0;


    public LuceneCursor(FieldMappings fieldMappings, LeafReader leafReader) throws IOException {
        this.fieldMappings = fieldMappings;
        this.leafReader = leafReader;

        IndexSearcher searcher = new IndexSearcher(leafReader);
        DocListCollector collector = new DocListCollector();
        searcher.search(new MatchAllDocsQuery(), collector);
        docList = collector.getDocList();
        done = docList.isEmpty();
        maxDocOffset = docList.size() - 1;
    }

    public boolean isDone() {
        return done;
    }

    public void advance() {
        if (maxDocOffset > offset) {
            offset += 1;
        } else {
            done = true;
        }
    }

    public int getCurrentDoc() {
        return docList.get(offset);
    }

    public DimensionSelector makeDimensionSelector(String dimension) throws IOException {
        FieldMappings.Field field = fieldMappings.getField(dimension);
        switch (field.getType()) {
            case "string":
                return makeStringDimensionSelector(dimension, field.isHasMultipleValues());
            case "int":
                return makeIntDimensionSelector(dimension, field.isHasMultipleValues());
            case "float":
                return makeFloatDimensionSelector(dimension, field.isHasMultipleValues());
            case "long":
                return makeLongDimensionSelector(dimension, field.isHasMultipleValues());
            default:
                return null;
        }
    }

    public StringDimensionSelector makeStringDimensionSelector(String dimension, boolean hasMultipleValues) throws IOException {
        if(hasMultipleValues) {
            return new StringMultiDimensionSelector(this, leafReader, dimension);
        } else {
            return new StringSingleDimensionSelector(this, leafReader, dimension);
        }
    }

    public IntDimensionSelector makeIntDimensionSelector(String dimension, boolean hasMultipleValues) throws IOException {
        if(hasMultipleValues) {
            return new IntMultiDimensionSelector(this, leafReader, dimension);
        } else {
            return new IntMultiDimensionSelector(this, leafReader, dimension);
        }
    }

    public LongDimensionSelector makeLongDimensionSelector(String dimension, boolean hasMultipleValues) throws IOException {
        if(hasMultipleValues) {
            return new LongSingleDimensionSelector(this, leafReader, dimension);
        } else {
            return new LongMultiDimensionSelector(this, leafReader, dimension);
        }
    }

    public FloatDimensionSelector makeFloatDimensionSelector(String dimension, boolean hasMultipleValues) throws IOException {
        if(hasMultipleValues) {
            return new FloatMultiDimensionSelector(this, leafReader, dimension);
        } else {
            return new FloatSingleDimensionSelector(this, leafReader, dimension);
        }
    }

}
