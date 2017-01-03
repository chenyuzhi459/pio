package io.sugo.pio.spark.transfer;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.BinominalMapping;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.example.table.PolynominalMapping;
import io.sugo.pio.spark.transfer.model.ModelTransferObject;

import java.util.*;

/**
 */
public class MTOConverter {
    public static void setNominalMappings(ModelTransferObject mto, ExampleSet exampleSet) {
        Map mtoMapping = mto.getNominalMapping();
        Iterator it = exampleSet.getAttributes().allAttributes();

        while(true) {
            while(true) {
                Attribute attribute;
                do {
                    if(!it.hasNext()) {
                        return;
                    }

                    attribute = (Attribute)it.next();
                } while(!mtoMapping.containsKey(attribute.getName()));

                if(attribute.isNominal()) {
                    NominalMapping existingMapping = attribute.getMapping();
                    HashMap nominalValueMap = new HashMap();
                    List attributeMapping = (List)mtoMapping.get(attribute.getName());

                    for(int mapping = 0; mapping < attributeMapping.size(); ++mapping) {
                        nominalValueMap.put(Integer.valueOf(mapping), attributeMapping.get(mapping));
                    }

                    Object var9;
                    if(existingMapping != null && existingMapping instanceof BinominalMapping) {
                        if(nominalValueMap.size() != 2 && (nominalValueMap.size() != 3 || nominalValueMap.get(Integer.valueOf(2)) != null)) {
                            throw new IllegalArgumentException("Nominal mapping for binominal attribute " + attribute.getName() + " contains " + nominalValueMap.size() + " values.");
                        }

                        var9 = new BinominalMapping();
                        ((NominalMapping)var9).mapString((String)nominalValueMap.get(Integer.valueOf(0)));
                        ((NominalMapping)var9).mapString((String)nominalValueMap.get(Integer.valueOf(1)));
                    } else {
                        nominalValueMap.values().remove(null);
                        var9 = new PolynominalMapping(nominalValueMap);
                    }

                    attribute.setMapping((NominalMapping)var9);
                }
            }
        }
    }
}
