package io.sugo.pio.spark.connections;

import java.util.*;
import java.util.stream.Collectors;

/**
 */
public class KeyValueEnableElement implements Comparable<KeyValueEnableElement> {
    public String key;
    public String value;
    public boolean enabled;

    public KeyValueEnableElement(String key, String value, boolean enabled) {
        this.key = key;
        this.value = value;
        this.enabled = enabled;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{key, value, Boolean.valueOf(enabled)});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof KeyValueEnableElement)) {
            return false;
        } else {
            KeyValueEnableElement other = (KeyValueEnableElement) obj;
            return Objects.equals(key, other.key) && Objects.equals(value, other.value) && enabled == other.enabled;
        }
    }

    public int compareTo(KeyValueEnableElement that) {
        int result = key.compareTo(that.key);
        if (result != 0) {
            return result;
        } else {
            result = value.compareTo(that.value);
            if (result != 0) {
                return result;
            } else {
                result = Boolean.compare(enabled, that.enabled);
                return result;
            }
        }
    }

    public String toString() {
        return "KeyValueEnableElement(key: " + key + ", value: " + value + ", enabled: " + enabled + ")";
    }

    public static class KVEEMap extends TreeMap<String, KeyValueEnableElement> implements Iterable<KeyValueEnableElement> {
        public KVEEMap() {
            super(String.CASE_INSENSITIVE_ORDER);
        }

        public KeyValueEnableElement put(KeyValueEnableElement element) {
            return super.put(element.key, element);
        }

        public KeyValueEnableElement add(KeyValueEnableElement element) {
            return put(element);
        }

        public KeyValueEnableElement putIfAbsent(KeyValueEnableElement element) {
            return super.putIfAbsent(element.key, element);
        }

        public String getValue(String key) {
            if (key == null) {
                return null;
            } else {
                KeyValueEnableElement element = get(key.trim());
                return element == null ? null : element.value;
            }
        }

        public boolean hasValue(String key) {
            String value = this.getValue(key);
            return value != null && value.trim().length() > 0;
        }


        public KeyValueEnableElement put(String key, KeyValueEnableElement element) {
            return put(element);
        }

        public void putAll(Map<? extends String, ? extends KeyValueEnableElement> map) {
            super.putAll(map);
        }


        public boolean contains(KeyValueEnableElement element) {
            return super.containsValue(element);
        }

        public Iterator<KeyValueEnableElement> iterator() {
            return super.values().iterator();
        }
    }
}