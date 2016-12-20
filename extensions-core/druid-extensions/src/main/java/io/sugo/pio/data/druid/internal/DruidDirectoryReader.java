package io.sugo.pio.data.druid.internal;

import com.google.common.collect.Maps;
import com.metamx.common.Pair;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.store.Directory;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class DruidDirectoryReader {
    private final LuceneCursor cursor;
    private final LongDimensionSelector timeDimensionSelector;
    private final List<DimensionSelector> dimensionSelectors;

    public DruidDirectoryReader(FieldMappings fieldMappings, Directory directory) throws IOException {
        LeafReader leafReader = DirectoryReader.open(directory).getContext().leaves().get(0).reader();
        cursor = new LuceneCursor(fieldMappings, leafReader);
        timeDimensionSelector = cursor.makeLongDimensionSelector("__time", false);
        Map<String, FieldMappings.Field> fieldMap = fieldMappings.getFieldMap();
        dimensionSelectors = new ArrayList<>(fieldMap.size());
        for(Map.Entry<String, FieldMappings.Field> entry: fieldMap.entrySet()) {
            dimensionSelectors.add(cursor.makeDimensionSelector(entry.getValue().getName()));
        }
    }

    public Pair<DateTime, Map<String, Object>> read() {
        if (cursor.isDone()) {
            return new Pair<>(new DateTime(0), null);
        }

        long time = timeDimensionSelector.getValues().get(0);
        Map<String, Object> event = Maps.newHashMap();
        for(DimensionSelector dimensionSelector : dimensionSelectors) {
            if (dimensionSelector.isMultiple()) {
                event.put(dimensionSelector.getDimensionName(), dimensionSelector.getValues());
            } else {
                event.put(dimensionSelector.getDimensionName(), dimensionSelector.getValue());
            }
        }

        cursor.advance();
        return new Pair<>(new DateTime(time), event);
    }

    public void close() {
    }

}
