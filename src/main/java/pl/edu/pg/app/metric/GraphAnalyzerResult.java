package pl.edu.pg.app.metric;

import lombok.Value;
import org.graphstream.graph.Node;

@Value
public class GraphAnalyzerResult {
    private int leafs;
    private Node root;
}
