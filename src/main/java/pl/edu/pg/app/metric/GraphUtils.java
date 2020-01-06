package pl.edu.pg.app.metric;

import lombok.NoArgsConstructor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.pg.app.metric.GraphAttribute.LABEL;
import static pl.edu.pg.app.metric.GraphAttribute.LEAF;

@NoArgsConstructor
public class GraphUtils {

    public static boolean isLeaf(Node node) {
        return node.<Boolean>getAttribute(LEAF.getText()).equals(Boolean.TRUE);
    }

    public static Node getSecondNodeFromEdge(Node currentNode, Edge edge) {
        return edge.getNode0().equals(currentNode) ? edge.getNode1() : edge.getNode0();
    }

    public static List<Node> getNodeChildren(Node node) {
        final List<Node> children = new ArrayList<>();
        for (Edge leavingEdge : node.getLeavingEdgeSet()) {
            Node child = getSecondNodeFromEdge(node, leavingEdge);
            children.add(child);
        }
        return children;
    }

    public static Edge getEdgeToParent(Node currentNode, Node child1, Node child2) {
        for (Edge edge : currentNode.getEdgeSet()) {
            if (!getSecondNodeFromEdge(currentNode, edge).equals(child1) && !getSecondNodeFromEdge(currentNode, edge).equals(child2)) {
                return edge;
            }
        }
        throw new IllegalStateException("Cannot find edge to parent");
    }

    public static void appendLabelToElement(Element node, String value) {
        String currVal = node.getAttribute(LABEL.getText());
        currVal = (currVal == null) ? "" : currVal + ", ";
        node.setAttribute(LABEL.getText(), currVal + value);
    }
}
