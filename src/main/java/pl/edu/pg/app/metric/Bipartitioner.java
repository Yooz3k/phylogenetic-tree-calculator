package pl.edu.pg.app.metric;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pl.edu.pg.app.metric.GraphLabel.LABEL;
import static pl.edu.pg.app.metric.GraphUtils.getSecondNodeFromEdge;

public class Bipartitioner {

    private final Node root;
    private final int leafsCount;

    public Bipartitioner(Node root, int leafsCount) {
        this.root = root;
        this.leafsCount = leafsCount;
    }

    public List<Node> getNodesInPostOrderAndSetBipartitions(Graph g) {
        List<Node> nodes = new ArrayList<>();
        if (root != null) {
            postOrder(root, nodes);
        } else {
            postOrder(g.getNode(0), nodes);
        }
        return nodes;
    }

    void postOrder(Node currentNode, List<Node> nodes) {
        final Collection<Edge> leavingEdges = currentNode.getLeavingEdgeSet();
        final List<Node> children = new ArrayList<>();
        for (Edge leavingEdge : leavingEdges) {
            Node child = getSecondNodeFromEdge(currentNode, leavingEdge);
            children.add(child);
            postOrder(child, nodes);
        }
        nodes.add(currentNode);
        final int indexInPostOrder = nodes.size() - 1;
        if (indexInPostOrder < leafsCount) {
            final String leafBipartition = getLeafBipartition(indexInPostOrder);
            currentNode.setAttribute(LABEL.getText(), leafBipartition);
        } else {
            final String nonLeafBipartition = getNonLeafBipartition(children);
            currentNode.setAttribute(LABEL.getText(), nonLeafBipartition);
        }
    }

    private String getNonLeafBipartition(List<Node> children) {
        String s = "";
        String c1 = children.get(0).getAttribute(LABEL.getText());
        String c2 = children.get(1).getAttribute(LABEL.getText());
        for (int i = 0; i < leafsCount; i++) {
            s += (c1.charAt(i) == '1' | c2.charAt(i) == '1') == Boolean.TRUE ? '1' : '0';
        }
        return s;
    }

    private String getLeafBipartition(int indexInPostOrder) {
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
