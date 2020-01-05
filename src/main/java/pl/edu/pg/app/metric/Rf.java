package pl.edu.pg.app.metric;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.struct.AdjacencyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pl.edu.pg.app.metric.GraphLabel.*;
import static pl.edu.pg.app.metric.GraphUtils.*;

public class Rf {

    private Graph g;

    private int leafsCount = 0;
    private Node root;

    public Rf(Graph g, AdjacencyList adj) {
        this.g = g;
    }

    public void count() {
        markNodesAsLeafsAndRoot();
        markLeafEdgesAsTrivialPartitions();
        markEdgesAsPartitions();

        List<Node> nodesInPostorder = new Bipartitioner(root, leafsCount).getNodesInPostOrderAndSetBipartitions(g);


        g.display();
    }



    private void markEdgesAsPartitions() {
        for (Edge edge : g.getEdgeSet()) {
            if (edge.<Boolean>getAttribute(PARTITION.getText()) == null) {
                edge.addAttribute(PARTITION.getText(), true);
                edge.addAttribute(LABEL.getText(), "partition");
            }
        }
    }

    private void markLeafEdgesAsTrivialPartitions() {
        for (Node node : g.getNodeSet()) {
            if (isLeaf(node)) {
                node.getEdgeSet().forEach(e -> e.addAttribute(PARTITION.getText(), false));
                node.getEdgeSet().forEach(e -> e.addAttribute(LABEL.getText(), "triv"));
            }
        }
    }

    private void markNodesAsLeafsAndRoot() {
        for (int i = 0; i < g.getNodeCount(); i++) {
            final Node node = g.getNode(i);
            if (getNodeChildren(node).isEmpty()) {
                node.setAttribute(LABEL.getText(), "leaf " + i);
                node.setAttribute(LEAF.getText(), true);
                leafsCount++;
            } else if (node.getDegree() == 2) {
                node.setAttribute(ROOT.getText(), true);
                node.setAttribute(LABEL.getText(), "ROOT");
                node.setAttribute(LEAF.getText(), false);
                node.addAttribute("ui.style", "fill-color: red;");
                root = node;
            } else {
                node.setAttribute(LABEL.getText(), i);
                node.setAttribute(LEAF.getText(), false);
            }
        }
    }
}

