package pl.edu.pg.app.metric;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pl.edu.pg.app.metric.GraphLabel.BIT_PARTITION;
import static pl.edu.pg.app.metric.GraphLabel.LABEL;
import static pl.edu.pg.app.metric.GraphLabel.LEAF;
import static pl.edu.pg.app.metric.GraphUtils.getSecondNodeFromEdge;
import static pl.edu.pg.app.metric.GraphUtils.isLeaf;

public class BiPartitioner {

    private final Node root;
    private final int leafsCount;
    private int leafNumber;

    public BiPartitioner(Node root, int leafsCount) {
        this.root = root;
        this.leafsCount = leafsCount;
    }

    public BiPartitionerResult getNodesInPostOrderAndSetBipartitions(Graph g) {
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
        final Collection<Edge> leavingEdges = currentNode.getLeavingEdgeSet();
        final List<Node> children = new ArrayList<>();
        for (Edge leavingEdge : leavingEdges) {
            Node child = getSecondNodeFromEdge(currentNode, leavingEdge);
            children.add(child);
            postOrder(child, nodes, partitions);
        }
        nodes.add(currentNode);
        setNodeBiPartition(currentNode, nodes, children, partitions);
    }

    private void setNodeBiPartition(Node currentNode, List<Node> nodes, List<Node> children, Set<String> partitions) {
        if (isLeaf(currentNode)) {
            final String leafBiPartition = getLeafBiPartition(leafNumber++);
            currentNode.setAttribute(LABEL.getText(), leafBiPartition);
            currentNode.setAttribute(BIT_PARTITION.getText(), leafBiPartition);
        } else {
            final String nonLeafBiPartition = getNonLeafBiPartition(children);
            currentNode.setAttribute(LABEL.getText(), nonLeafBiPartition);
            currentNode.setAttribute(BIT_PARTITION.getText(), nonLeafBiPartition);
            partitions.add(nonLeafBiPartition);
        }
    }

    private String getNonLeafBiPartition(List<Node> children) {
        String s = "";
        String c1 = children.get(0).getAttribute(LABEL.getText());
        String c2 = children.get(1).getAttribute(LABEL.getText());
        for (int i = 0; i < leafsCount; i++) {
            s += (c1.charAt(i) == '1' | c2.charAt(i) == '1') == Boolean.TRUE ? '1' : '0';
        }
        return s;
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
