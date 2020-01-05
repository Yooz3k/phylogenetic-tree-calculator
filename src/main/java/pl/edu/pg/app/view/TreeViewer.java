package pl.edu.pg.app.view;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.io.GraphLoader;

public class TreeViewer {

    public void view(String filename) {
        String filepath = GraphLoader.getFilePath(filename);
        Graph g = GraphLoader.load(filepath);

        g.getNodeSet().forEach(node -> node.addAttribute("label", node.getId()));

        g.display();
    }
}
