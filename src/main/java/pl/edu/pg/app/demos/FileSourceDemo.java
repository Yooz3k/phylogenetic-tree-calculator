package pl.edu.pg.app.demos;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphConverter;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class FileSourceDemo {
    private static final String FILENAME = "graph_with_attributes.xml";

    public static void main(String[] args) throws URISyntaxException {
        String filepath = getFilePath( FILENAME );

        Graph g = GraphLoader.load(filepath);

        GraphConverter<AdjacencyList> converter = new GraphToAdjListConverter();
        AdjacencyList adjList = converter.convert(g);

        //Print converted graph
        System.out.println("Adjacency list:\n" + adjList.toString());

        //Display loaded graph
        g.display();
    }

    protected static String getFilePath( String path ) throws URISyntaxException {
        URL res = FileSourceDemo.class.getClassLoader().getResource( path );
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }
}