package io.sugo.pio;

import java.util.HashSet;
import java.util.Set;

/**
 */
public enum SparkClassLoader
{
    CLASSLOADER_12("classloader_1.2"),
    CLASSLOADER_15("classloader_1.5"),
    CLASSLOADER_20("classloader_2.0");

    private final String id;
    private final Set<String> jars = new HashSet();

    private SparkClassLoader(String id) {
        this.id = id;
    }

    public void addJar(String jar) {
        this.jars.add(jar);
    }

    public Set<String> getJars() {
        return this.jars;
    }

    public String getId() {
        return this.id;
    }

    public static SparkClassLoader getFromId(String id) {
        if (id == null) {
            return CLASSLOADER_12;
        }
        for (SparkClassLoader cl : values()) {
            if (id.equals(cl.getId())) {
                return cl;
            }
        }
        return null;
    }
}