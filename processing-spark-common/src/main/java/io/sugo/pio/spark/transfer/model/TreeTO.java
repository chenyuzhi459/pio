package io.sugo.pio.spark.transfer.model;

import io.sugo.pio.spark.transfer.TransferObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class TreeTO extends TransferObject {
    private String label;
    private List<EdgeTO> children = new ArrayList();
    private Map<String, Integer> counterMap = new HashMap();

    public TreeTO() {
    }

    public TreeTO(String label, List<EdgeTO> children, Map<String, Integer> counterMap) {
        this.label = label;
        this.children = children;
        this.counterMap = counterMap;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<EdgeTO> getChildren() {
        return this.children;
    }

    public void setChildren(List<EdgeTO> children) {
        this.children = children;
    }

    public Map<String, Integer> getCounterMap() {
        return this.counterMap;
    }

    public void setCounterMap(Map<String, Integer> counterMap) {
        this.counterMap = counterMap;
    }
}
