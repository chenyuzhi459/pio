package sugo.io.pio.data.druid.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 */
public class FieldMappings {
    private static final String FILE_NAME = ".mapping";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, Field> fieldMap;

    public Field getField(String dimension) {
        return fieldMap.get(dimension);
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public static FieldMappings buildFrom(FileSystem fs, Path path) throws IOException {
        ZipInputStream stream = new ZipInputStream(fs.open(path));
        ZipEntry tmpEntry;
        while(null != (tmpEntry = (stream.getNextEntry()))) {
            if (tmpEntry.getName().equals(FILE_NAME)) {
                break;
            }
        }
        List<Field> fields = MAPPER.readValue(stream, new TypeReference<List<Field>>() {});
        return new FieldMappings(getFieldTypes(fields));
    }

    public static FieldMappings buildFrom(String path) throws IOException {
        ZipFile zipfile = new ZipFile(path);
        ZipEntry entry = zipfile.getEntry(FILE_NAME);
        InputStream stream = zipfile.getInputStream(entry);
        List<Field> fields = MAPPER.readValue(stream, new TypeReference<List<Field>>() {});
        return new FieldMappings(getFieldTypes(fields));
    }

    private static Map<String, Field> getFieldTypes(List<Field> fields) throws IOException {
        Map<String, Field> fieldTypes = Maps.newHashMap();
        for (Field field : fields) {
            fieldTypes.put(field.getName(), field);
        }
        return fieldTypes;
    }

    public FieldMappings(Map<String, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public static class Field {
        private String name;
        private String type;
        private boolean hasMultipleValues;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public boolean isHasMultipleValues() {
            return hasMultipleValues;
        }
    }
}
