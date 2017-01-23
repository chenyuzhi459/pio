package io.sugo.pio.engine.ocb;

import java.util.List;
/**
 */
public class Similarity {
    public Double getSimilarity(List<String> features1,List<String> features2){
        Double similarity = 0.0;
        int length = features1.size();
        for(int i=0;i<length;i++)
            similarity += Math.abs(Double.parseDouble(features1.get(i)) - Double.parseDouble(features2.get(i)));
        return similarity;
    }
}

