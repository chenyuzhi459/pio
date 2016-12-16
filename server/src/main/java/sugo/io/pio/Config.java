package sugo.io.pio;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;


public class Config {
    public static String ENGIN_ID ="engin.model";
    public static String DATA_SOURCE_ID="datasource.model";
    public static String TASK_MODEL = "task.model";
    public static String DB_URL = "db.url";
    public static String OUTPUT_MODEL = "output.model";
    private static Map<String,String> kvs = new HashedMap();

    public static void set (String key,String value){
        kvs.put(key,value);
    }

    public static String get(String key){
        return kvs.get(key);
    }



}
