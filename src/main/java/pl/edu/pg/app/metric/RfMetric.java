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
    private static final String FILENAME = "rf-graph-root.xml";

    public static void main(String[] args) throws URISyntaxException {
        String filepath = getFilePath();

        Graph g = GraphLoader.load(filepath);

        GraphConverter<AdjacencyList> converter = new GraphToAdjListConverter();
        AdjacencyList adj = converter.convert(g);
        //Print converted graph
        System.out.println("Adjacency list:\n" + adj.toString());

        new Rf(g, adj).count();
    }



    private static String getFilePath() throws URISyntaxException {
        URL res = FileSourceDemo.class.getClassLoader().getResource(FILENAME);
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }
}
