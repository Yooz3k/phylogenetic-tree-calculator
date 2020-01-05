package pl.edu.pg.app.metric;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.demos.FileSourceDemo;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RfMetric {
    private static final String FILENAME = "rf-graph2.xml";
    private static final String LABEL = "label";
    private static final String LEAF = "leaf";
    public static final String PARTITION = "partition";

    public static void main(String[] args) throws URISyntaxException {
        String filepath = getFilePath();

        Graph g = GraphLoader.load(filepath);

        GraphConverter<AdjacencyList> converter = new GraphToAdjListConverter();
        AdjacencyList adj = converter.convert(g);

        for (int i = 0; i < adj.getSize(); i++) {
            final Node node = g.getNode(i);
            if (adj.get(i).size() == 1) {
                node.setAttribute(LABEL, "leaf " + i);
                node.setAttribute(LEAF, true);
            } else {
                node.setAttribute(LABEL, i);
                node.setAttribute(LEAF, false);
            }
        }

        for (Node node : g.getNodeSet()) {
            if (node.<Boolean>getAttribute(LEAF).equals(Boolean.TRUE)) {
                node.getEdgeSet().forEach(e -> e.addAttribute(PARTITION, false));
                node.getEdgeSet().forEach(e -> e.addAttribute(LABEL, "non-part"));
            }
        }

        for (Edge edge : g.getEdgeSet()) {
            final Boolean val = edge.<Boolean>getAttribute(PARTITION);
            if (val == null) {
                edge.addAttribute(PARTITION, true);
                edge.addAttribute(LABEL, "partition");
            }
        }



        //Print converted graph
        System.out.println("Adjacency list:\n" + adj.toString());

        //Display loaded graph
        g.display();

    }

    private static String getFilePath() throws URISyntaxException {
        URL res = FileSourceDemo.class.getClassLoader().getResource(FILENAME);
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }
}
