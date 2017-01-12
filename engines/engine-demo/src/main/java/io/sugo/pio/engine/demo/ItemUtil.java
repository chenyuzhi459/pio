package io.sugo.pio.engine.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtil {
    private static Map<String, String> item2Titile = new HashMap<>();
    private static Map<String, List<String>> item2Tags = new HashMap<>();
    private static final String SEPERATOR = "\\|";
    private static final String[] ITEM_GENS = "unknown|Action|Adventure|Animation|Children's|Comedy|Crime|Documentary|Drama|Fantasy|Film-Noir|Horror|Musical|Mystery|Romance|Sci-Fi|Thriller|War|Western|".split(SEPERATOR);


    static {
        InputStream inputStream = ItemUtil.class.getClassLoader().getResourceAsStream("movielen100k/u.item");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(SEPERATOR);
                String title = tokens[1];
                if (title.indexOf("(") > 0) {
                    int index = title.indexOf("(");
                    title = title.substring(0, index).trim();
                }
                item2Titile.put(tokens[0], title);

                List<String> gens = new ArrayList<>();
                for (int i=5;i<tokens.length;i++) {
                    String flag = tokens[i];
                    if ("1".equals(flag)) {
                        String gen = ITEM_GENS[i - 5];
                        gens.add(gen);
                    }
                }
                item2Tags.put(tokens[0], gens);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTitle(String itemId) {
        return item2Titile.get(itemId);
    }

    public static List<String> getTags(String itemId) {
        return item2Tags.get(itemId);
    }
}
