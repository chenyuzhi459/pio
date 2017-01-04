package io.sugo.pio.spark.transfer.model;

import java.util.List;
import java.util.Map;

/**
 */
public class TreeModelMTO extends ModelTransferObject {
    private TreeTO root;

    public TreeModelMTO(TreeTO tree, Map<String, List<String>> nominalMapping) {
        super(nominalMapping);
        this.root = tree;
    }

    public TreeTO getRoot() {
        return this.root;
    }

    public void setRoot(TreeTO root) {
        this.root = root;
    }
}
