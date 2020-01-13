package pl.edu.pg.app.metric;

import lombok.NoArgsConstructor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.demos.FileSourceDemo;
import pl.edu.pg.app.io.GraphLoader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.pg.app.metric.GraphAttribute.LABEL;

@NoArgsConstructor
public class GraphUtils {

    public static boolean isLeaf(Node node) {
        return node.getDegree() == 1;
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

    public static Edge getEdgeToParent(Node currentNode, List<Node> children) {
        for (Edge edge : currentNode.getEdgeSet()) {
            int edgeToChildCount = 0;
            for (Node child : children) {
                if (getSecondNodeFromEdge(currentNode, edge).equals(child)) {
                    edgeToChildCount++;
                    break;
                }
            }
            if (edgeToChildCount == 0) {
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

    public static void incrementVisitedValue(Element node) {
        int val = getVisitedValue(node);
        val++;
        node.setAttribute("VISITED", val);
    }

    public static int getVisitedValue(Element node) {
        Integer val = node.getAttribute("VISITED", Integer.class);
        if (val == null) {
            val = 0;
        }
        return val;
    }


    public static Graph loadGraph(String filename) throws URISyntaxException {
        String filepath = getFilePath(filename);
        Graph g = GraphLoader.load(filepath, true);
        return g;
    }


    private static String getFilePath(String filename) throws URISyntaxException {
        URL res = FileSourceDemo.class.getClassLoader().getResource(filename);
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }
}
