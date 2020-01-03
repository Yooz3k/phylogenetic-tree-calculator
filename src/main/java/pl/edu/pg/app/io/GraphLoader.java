package pl.edu.pg.app.io;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;

public class GraphLoader {

    public static Graph load(String filepath) {
        Graph graph = new DefaultGraph("g");

        FileSource fs = null;
        try {
            fs = FileSourceFactory.sourceFor(filepath);
            fs.addSink(graph);
            fs.readAll(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                fs.removeSink(graph);
            }
        }

        return graph;
    }
}
