package io.sugo.pio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public enum SparkVersion {
    DUMMY_VERSION(null, "Select Spark version...", new String[0], SparkClassLoader.CLASSLOADER_12),

    NONE(null, "None", new String[0], SparkClassLoader.CLASSLOADER_12),
    SPARK_12("spark_1.2", "Spark 1.4 or below", new String[]{"1.2", "1.3", "1.4"}, SparkClassLoader.CLASSLOADER_12),

    SPARK_15("spark_1.5", "Spark 1.5", new String[]{"1.5"}, SparkClassLoader.CLASSLOADER_15),
    SPARK_16("spark_1.6", "Spark 1.6", new String[]{"1.6"}, SparkClassLoader.CLASSLOADER_15),
    SPARK_20("spark_2.0", "Spark 2.0", new String[]{"2.0"}, SparkClassLoader.CLASSLOADER_20);

    private final String name;
    private final String id;
    private final String[] scVersionStrings;
    private final SparkClassLoader classLoader;

    private SparkVersion(String id, String name, String[] scVersionStrings, SparkClassLoader classLoader) {
        this.id = id;
        this.name = name;
        this.scVersionStrings = scVersionStrings;
        this.classLoader = classLoader;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }


    public static SparkVersion getFromId(String id) {
        if (id == null) {
            return NONE;
        }
        for (SparkVersion spark : values()) {
            if (id.equals(spark.getId())) {
                return spark;
            }
        }
        return null;
    }

    public static SparkVersion[] get15AndAbove() {
        List above15List = new ArrayList(Arrays.asList(values()));
        above15List.removeAll(Arrays.asList(new SparkVersion[] { DUMMY_VERSION, NONE, SPARK_12 }));
        return (SparkVersion[])above15List.toArray(new SparkVersion[0]);
    }

    public static SparkVersion[] get20AndAbove() {
        List above20List = new ArrayList(Arrays.asList(values()));
        above20List.removeAll(Arrays.asList(new SparkVersion[] { DUMMY_VERSION, NONE, SPARK_12, SPARK_15, SPARK_16 }));
        return (SparkVersion[])above20List.toArray(new SparkVersion[0]);
    }

    public boolean is15OrAbove() {
        for (SparkVersion ver : get15AndAbove()) {
            if (ver.equals(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean is20OrAbove() {
        for (SparkVersion ver : get20AndAbove()) {
            if (ver.equals(this)) {
                return true;
            }
        }
        return false;
    }

}
