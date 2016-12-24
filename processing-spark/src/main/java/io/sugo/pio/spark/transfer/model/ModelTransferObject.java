package io.sugo.pio.spark.transfer.model;

import io.sugo.pio.spark.transfer.TransferObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ModelTransferObject extends TransferObject {
    private Map<String, List<String>> nominalMapping = new HashMap();

    public ModelTransferObject(Map<String, List<String>> nominalMapping) {
        this.nominalMapping = nominalMapping;
    }

    public Map<String, List<String>> getNominalMapping() {
        return this.nominalMapping;
    }

    public void setNominalMapping(Map<String, List<String>> nominalMapping) {
        this.nominalMapping = nominalMapping;
    }
}
