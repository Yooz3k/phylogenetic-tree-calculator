package pl.edu.pg.app.metric;

import lombok.Data;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pl.edu.pg.app.metric.GraphAttribute.BIT_PARTITION;
import static pl.edu.pg.app.metric.GraphUtils.appendLabelToElement;
import static pl.edu.pg.app.metric.GraphUtils.getEdgeToParent;
import static pl.edu.pg.app.metric.GraphUtils.getSecondNodeFromEdge;
import static pl.edu.pg.app.metric.GraphUtils.getVisitedValue;
import static pl.edu.pg.app.metric.GraphUtils.incrementVisitedValue;
import static pl.edu.pg.app.metric.GraphUtils.isLeaf;

public class BiPartitioner {

    private final int leafsCount;
    private int leafNumber;

    public BiPartitioner(int leafsCount) {
        this.leafsCount = leafsCount;
    }

    public BiPartitionerResult getNodesInPostOrderAndSetBiPartitions(Graph g, Node root) {
        leafNumber = 0;
        List<Node> nodes = new ArrayList<>();
        Set<String> partitions = new HashSet<>();
        if (root != null) {
            postOrder(root, nodes, partitions, new InitialConfiguration(root, g.getNodeCount(), root));
        } else {
            final Node initialNode = g.getNodeSet().stream().filter(f -> !isLeaf(f)).findAny().get();
            postOrder(initialNode, nodes, partitions, new InitialConfiguration(g.getNodeCount(), initialNode));
        }
        return new BiPartitionerResult(partitions, nodes);
    }

    void postOrder(Node currentNode, List<Node> nodes, Set<String> partitions, InitialConfiguration conf) {
        incrementVisitedValue(currentNode);
        final List<Node> children = new ArrayList<>();
        if (!isLeaf(currentNode)) {
            final Collection<Edge> leavingEdges = currentNode.getLeavingEdgeSet();
            for (Edge leavingEdge : leavingEdges) {
                Node child = getSecondNodeFromEdge(currentNode, leavingEdge);
                if (getVisitedValue(child) == 0) {
                    children.add(child);
                    postOrder(child, nodes, partitions, conf);
                }
            }
        }
        nodes.add(currentNode);
        setEdgeBiPartition(currentNode, children, partitions, conf, nodes.size());
    }

    private void setEdgeBiPartition(Node currentNode, List<Node> children, Set<String> partitions, final InitialConfiguration conf, int nodesVisited) {
        if (isLeaf(currentNode)) {
            final String leafBiPartition = getLeafBiPartition(leafNumber++);
            appendLabelToElement(currentNode, leafBiPartition);
            currentNode.setAttribute(BIT_PARTITION.getText(), leafBiPartition);
        } else if (!conf.hasRoot() || currentNode.getIndex() != conf.getRoot().getIndex()) {
            final String nonLeafBiPartition = getNonLeafBiPartition(currentNode, children);
            if (!conf.hasRoot() && returnedToInitialNode(currentNode, conf, nodesVisited)) {
                return;
            }
            final Edge edgeToParent = getEdgeToParent(currentNode, children);
            if (edgeToParent.getAttribute(BIT_PARTITION.getText()) == null && !isLeaf(getSecondNodeFromEdge(currentNode, edgeToParent))) {
                appendLabelToElement(edgeToParent, "part(" + nonLeafBiPartition + ")");
                edgeToParent.setAttribute(BIT_PARTITION.getText(), nonLeafBiPartition);
                partitions.add(nonLeafBiPartition);
            }
        }
    }

    private boolean returnedToInitialNode(Node currentNode, InitialConfiguration conf, int nodesVisited) {
        return nodesVisited == conf.getNodes() && conf.getInitialNode().equals(currentNode);
    }

    private String getNonLeafBiPartition(Node currentNode, List<Node> children) {
        String s = "";
        List<String> partitions = new ArrayList<>();
        for (Node n : children) {
            partitions.add(getBitPartitionFromNode(currentNode, n));
        }
        for (int i = 0; i < leafsCount; i++) {
            boolean found1 = false;
            for (String partition : partitions) {
                if (partition.charAt(i) == '1') {
                    found1 = true;
                    break;
                }
            }
            s += found1 == Boolean.TRUE ? '1' : '0';
        }
        return s;
    }

    private String getBitPartitionFromNode(Node currentNode, Node childNode) {
        if (isLeaf(childNode)) {
            return childNode.getAttribute(BIT_PARTITION.getText());
        } else {
            return childNode.getEdgeFrom(currentNode.getIndex()).getAttribute(BIT_PARTITION.getText());
        }
    }

    private String getLeafBiPartition(int indexInPostOrder) {
        String s = "";
        for (int i = 0; i < leafsCount; i++) {
            if (i == indexInPostOrder) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }

    @Data
    private class InitialConfiguration {
        private final int nodes;
        private final Node initialNode;
        private Node root;

        public InitialConfiguration(Node root, int nodes, Node initialNode) {
            this.nodes = nodes;
            this.initialNode = initialNode;
            this.root = root;
        }

        public InitialConfiguration(int nodes, Node initialNode) {
            this.nodes = nodes;
            this.initialNode = initialNode;
        }

        public boolean hasRoot() {
            return root != null;
        }
    }
}
