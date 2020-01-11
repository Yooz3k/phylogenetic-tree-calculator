package pl.edu.pg.app.metric;

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
import static pl.edu.pg.app.metric.GraphUtils.getGoValue;
import static pl.edu.pg.app.metric.GraphUtils.getSecondNodeFromEdge;
import static pl.edu.pg.app.metric.GraphUtils.incrementGoValue;
import static pl.edu.pg.app.metric.GraphUtils.isLeaf;

public class BiPartitioner {

    private final Node root;
    private final int leafsCount;
    private int leafNumber;

    public BiPartitioner(Node root, int leafsCount) {
        this.root = root;
        this.leafsCount = leafsCount;
    }

    public BiPartitionerResult getNodesInPostOrderAndSetBiPartitions(Graph g) {
        leafNumber = 0;
        List<Node> nodes = new ArrayList<>();
        Set<String> partitions = new HashSet<>();
        if (root != null) {
            postOrder(root, nodes, partitions);
        } else {
            postOrder(g.getNodeSet().stream().filter(f -> !isLeaf(f)).findAny().get(), nodes, partitions);
        }
        return new BiPartitionerResult(partitions, nodes);
    }

    void postOrder(Node currentNode, List<Node> nodes, Set<String> partitions) {
        incrementGoValue(currentNode);
        final List<Node> children = new ArrayList<>();
        if (!isLeaf(currentNode)) {
            final Collection<Edge> leavingEdges = currentNode.getLeavingEdgeSet();
            for (Edge leavingEdge : leavingEdges) {
                Node child = getSecondNodeFromEdge(currentNode, leavingEdge);
                if (getGoValue(child) == 0) {
                    children.add(child);
                    postOrder(child, nodes, partitions);
                }
            }
        }
        nodes.add(currentNode);
        setEdgeBiPartition(currentNode, children, partitions);
    }

    private void setEdgeBiPartition(Node currentNode, List<Node> children, Set<String> partitions) {
        if (isLeaf(currentNode)) {
            final String leafBiPartition = getLeafBiPartition(leafNumber++);
            appendLabelToElement(currentNode, leafBiPartition);
            currentNode.setAttribute(BIT_PARTITION.getText(), leafBiPartition);
        } else if (root == null || currentNode.getIndex() != root.getIndex()) {
            final String nonLeafBiPartition = getNonLeafBiPartition(currentNode, children);
            final Edge edgeToParent = getEdgeToParent(currentNode, children);
            if (edgeToParent.getAttribute(BIT_PARTITION.getText()) == null && !isLeaf(getSecondNodeFromEdge(currentNode, edgeToParent))) {
                appendLabelToElement(edgeToParent, "part(" + nonLeafBiPartition + ")");
                edgeToParent.setAttribute(BIT_PARTITION.getText(), nonLeafBiPartition);
                partitions.add(nonLeafBiPartition);
            }
        }
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
}
