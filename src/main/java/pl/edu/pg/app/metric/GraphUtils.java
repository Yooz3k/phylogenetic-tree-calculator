package pl.edu.pg.app.metric;

import lombok.NoArgsConstructor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.pg.app.metric.GraphLabel.LEAF;

@NoArgsConstructor
public class GraphUtils {

    public static boolean isLeaf(Node node) {
        return node.<Boolean>getAttribute(LEAF.getText()).equals(Boolean.TRUE);
    }

    public static Node getSecondNodeFromEdge(Node currentNode, Edge leavingEdge) {
        return leavingEdge.getNode0().equals(currentNode) ? leavingEdge.getNode1() : leavingEdge.getNode0();
    }

    public static List<Node> getNodeChildren(Node node) {
        final List<Node> children = new ArrayList<>();
        for (Edge leavingEdge : node.getLeavingEdgeSet()) {
            Node child = getSecondNodeFromEdge(node, leavingEdge);
            children.add(child);
        }
        return children;
    }
}
