package pl.edu.pg.app.io;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import pl.edu.pg.app.Application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphLoader {

    public static Graph load(String filepath) {
        Graph graph = new DefaultGraph("g");

        FileSource fs = null;
        try {
            fs = FileSourceFactory.sourceFor(filepath);
            fs.addSink(graph);
            fs.readAll(filepath);
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku ze ścieżki " + filepath + ":");
            e.printStackTrace();
        } finally {
            if (fs != null) {
                fs.removeSink(graph);
            }
        }

        return graph;
    }

    public static List<Graph> loadMultiple(List<String> filenames) {
        List<String> filepaths = new ArrayList<>();
        for (String filename : filenames) {
            String filePath = getFilePath(filename);
            filepaths.add(filePath);
        }

        return filepaths.stream()
                .map(GraphLoader::load)
                .collect(Collectors.toList());
    }

    public static String getFilePath(String filename) {
        URL res = Application.class.getClassLoader().getResource(filename);
        File file;
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (URISyntaxException e) {
            System.err.println("Błąd podczas odczytu pliku o nazwie " + filename + ":");
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();
    }
}
