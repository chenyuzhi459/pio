package io.sugo.pio.operator.extension.jdbc.tools.jdbc;

import java.util.HashMap;
import java.util.Map;

public class PostgreSQLUtils {
    static final Map<Integer, DataTypeSyntaxInformation> POSTGRES_DATA_TYPES = new HashMap();

    private PostgreSQLUtils() {
        throw new AssertionError("Utility class must not be instantiated!");
    }

    static {
        POSTGRES_DATA_TYPES.put(Integer.valueOf(12), new DataTypeSyntaxInformation("\'", "\'", 12, "text", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(2005), new DataTypeSyntaxInformation("\'", "\'", 12, "text", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(2004), new DataTypeSyntaxInformation("\'", "\'", 12, "text", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(-16), new DataTypeSyntaxInformation("\'", "\'", 12, "text", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(-1), new DataTypeSyntaxInformation("\'", "\'", 12, "text", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(8), new DataTypeSyntaxInformation((String)null, (String)null, 8, "float8", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(7), new DataTypeSyntaxInformation((String)null, (String)null, 8, "float8", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(6), new DataTypeSyntaxInformation((String)null, (String)null, 8, "float8", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(4), new DataTypeSyntaxInformation((String)null, (String)null, 4, "int4", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(91), new DataTypeSyntaxInformation("\'", "\'", 91, "date", (String)null, 0L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(93), new DataTypeSyntaxInformation("\'", "\'", 93, "timestamp with time zone", (String)null, 6L));
        POSTGRES_DATA_TYPES.put(Integer.valueOf(92), new DataTypeSyntaxInformation("\'", "\'", 92, "time with time zone", (String)null, 6L));
    }
}
