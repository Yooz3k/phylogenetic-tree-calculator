package pl.edu.pg.app.metric;

import lombok.NoArgsConstructor;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.demos.FileSourceDemo;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

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

    public static void incrementGoValue(Element node) {
        int val = getGoValue(node);
        val++;
        node.setAttribute("GO", val);
    }

    public static int getGoValue(Element node) {
        Integer val = node.getAttribute("GO", Integer.class);
        if (val == null) {
            val = 0;
        }
        return val;
    }


    public static Graph loadGraph(String filename) throws URISyntaxException {
        String filepath = getFilePath(filename);
        Graph g = GraphLoader.load(filepath, true);
        GraphConverter<AdjacencyList> converter = new GraphToAdjListConverter();
        AdjacencyList adj = converter.convert(g);
        System.out.println("Adjacency list:\n" + adj.toString());
        return g;
    }


    private static String getFilePath(String filename) throws URISyntaxException {
        URL res = FileSourceDemo.class.getClassLoader().getResource(filename);
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }
}
