package io.sugo.pio.server.pathanalysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.server.pathanalysis.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
public class TreePlanter {

    public Set<AccessPath> accessPaths = Sets.newHashSet();

    private TreeHolder treeHolder = new TreeHolder();

    public void addPath(AccessPath path) {
        if (path == null) {
            return;
        }
        /*for (AccessPath path : accessPaths) {
            if (path.equals(newPath)) {
                path.increaseFrequency();

                int nodeSize = path.length();
                List<PathNode> nodeList = path.getNodeList();
                List<PathNode> newNodeList = newPath.getNodeList();
                for (int i = 0; i < nodeSize; i++) {
                    nodeList.get(i).accumulateStayTime(newNodeList.get(i).getStayTime());
                }
                return;
            }
        }*/

        accessPaths.add(path);
        growTree(path);
    }

    public AccessTree getRoot() {
        return treeHolder.getRoot();
    }

    private void growTree(AccessPath path) {
        List<PathNode> nodeList = path.getNodeList();

        AccessTree parentTree = null;
        for (PathNode currentNode : nodeList) {
            AccessTree currentTree = treeHolder.getTree(parentTree, currentNode);
            if (currentTree == null) {
                // Create a new sub tree
                currentTree = new AccessTree(TreeNode.convert(currentNode), parentTree,1);
                treeHolder.addTree(parentTree, currentTree);

                if (parentTree != null) {
                    parentTree.addChild(currentTree);
                }
            } else {
                // If the sub tree already exists, just increase the weight of it.
                currentTree.increaseWeight();

                // Accumulate tree leaf's total stay time and record all users who accessed this node
                currentTree.getLeaf().accumulateStayTime(currentNode.getStayTime());
                currentTree.getLeaf().addUserId(currentNode.getUserId());
            }

            parentTree = currentTree;
        }
    }

    private class TreeHolder {

        private Map<String, AccessTree> trees = Maps.newHashMap();

        public AccessTree getTree(AccessTree parentTree, Node leaf) {
            if (leaf == null) {
                return null;
            }

            String key = getKey(parentTree, leaf);
            return trees.get(key);
        }

        public void addTree(AccessTree parentTree, AccessTree tree) {
            TreeNode leaf = tree.getLeaf();
            trees.put(getKey(parentTree, leaf), tree);
        }

        public AccessTree getRoot() {
            if (accessPaths.size() > 0) {
                return getTree(null, accessPaths.iterator().next().getNodeList().get(0));
            }

            return null;
        }

        /**
         * To ensure a node only has unique parent, the generate rule of key used the whole chain.
         */
        private String getKey(AccessTree parentTree, Node leaf) {
            StringBuilder sb = new StringBuilder("");
            while (parentTree != null) {
                sb.append(parentTree.getLeaf().getPageName())
                        .append(".")
                        .append(parentTree.getLeaf().getLayer())
                        .append("_");
                parentTree = parentTree.getParent();
            }

            sb.append(leaf.getPageName())
                    .append(".")
                    .append(leaf.getLayer());

            return sb.toString();
        }
    }

    public static void main(String[] args) {
        ObjectMapper jsonMapper = new DefaultObjectMapper();

        TreePlanter paths = new TreePlanter();

        PathNode a = new PathNode("A", 1);
        PathNode b = new PathNode("B", 2);
        PathNode c = new PathNode("C", 3);
        AccessPath path = new AccessPath();
        path.addNode(a);
        path.addNode(b);
        path.addNode(c);
        paths.addPath(path);

        a = new PathNode("A", 1);
        b = new PathNode("B", 2);
        PathNode d = new PathNode("D", 3);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(b);
        path.addNode(d);
        paths.addPath(path);

        a = new PathNode("A", 1);
        b = new PathNode("B", 2);
        d = new PathNode("D", 3);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(b);
        path.addNode(d);
        paths.addPath(path);

        a = new PathNode("A", 1);
        c = new PathNode("C", 2);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(c);
        paths.addPath(path);

        a = new PathNode("A", 1);
        d = new PathNode("D", 2);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(d);
        paths.addPath(path);

        a = new PathNode("A", 1);
        b = new PathNode("B", 2);
        d = new PathNode("D", 3);
        PathNode f = new PathNode("F", 4);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(b);
        path.addNode(d);
        path.addNode(f);
        paths.addPath(path);

        a = new PathNode("A", 1);
        b = new PathNode("B", 2);
        d = new PathNode("D", 3);
        PathNode e = new PathNode("E", 4);
        path = new AccessPath();
        path.addNode(a);
        path.addNode(b);
        path.addNode(d);
        path.addNode(e);
        paths.addPath(path);

        AccessTree root = paths.getRoot();
        try {
            System.out.println(jsonMapper.writeValueAsString(root));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

    }
}
