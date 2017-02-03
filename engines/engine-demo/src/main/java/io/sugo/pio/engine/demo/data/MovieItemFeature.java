package io.sugo.pio.engine.demo.data;

import io.sugo.pio.engine.ocb.ItemFeature;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class MovieItemFeature implements ItemFeature {
    private static final Map<String,List<String>> itemFeatures = new HashMap<>();
    private static final String SEPERATOR = "\\|";
    static {
        InputStream inputStream=MovieItemFeature.class.getClassLoader().getResourceAsStream("movielen100k/features.item");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(SEPERATOR);
                String itemId = tokens[0];
                List<String> itemFeature = new ArrayList<>();
                for(int i =0;i< (tokens.length-1);i++)
                    itemFeature.add(tokens[i+1]) ;
                itemFeatures.put(itemId,itemFeature);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String>  getItemFeature(String itemId){
        return itemFeatures.get(itemId) ;
    }

}
