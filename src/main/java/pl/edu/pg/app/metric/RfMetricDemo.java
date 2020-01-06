package pl.edu.pg.app.metric;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.demos.FileSourceDemo;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;
import pl.edu.pg.app.view.TreeViewer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class RfMetricDemo {
    private static final String FILENAME = "rf-graph-root.xml";
    private static final String FILENAME2 = "rf-graph-root2.xml";

    public static void main(String[] args) throws URISyntaxException {
        Graph g = loadGraph(FILENAME);
        Graph g2 = loadGraph(FILENAME2);

        g.display();
        g2.display();

        new RfMetricCounter().count(g, g2);
    }

    private static Graph loadGraph(String filename) throws URISyntaxException {
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
