package pl.edu.pg.app.cutting;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.metric.GraphUtils;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

public class CuttingOffLeafs {

    public void cut(String filename, Set<String> nodeIds) {
        try {
            displayInitialGraph(filename);
            Graph g = GraphUtils.loadGraph(filename);
            throwExceptionIfIncorrectData(g, nodeIds);
            Optional<Node> root = findRoot(g);

            g.getNodeSet().forEach(n -> {
                if (GraphUtils.isLeaf(n) && !nodeIds.contains(n.getId())) {
                    g.removeNode(n);
                }
            });

            if (root.isPresent()) {
                inOrderCutOffNodes(root.get(), null, nodeIds, g);
            } else {

            }

            g.display();
        } catch (URISyntaxException e) {
            System.out.println("Problem ze ścieżką do pliku");
            e.printStackTrace();
        }

    }

    private void throwExceptionIfIncorrectData(Graph g, Set<String> nodeIds) {
        for (Node n : g.getNodeSet()) {
            if (nodeIds.contains(n.getId()) && !GraphUtils.isLeaf(n)) {
                throw new IllegalStateException("Not all nodes are leafs!");
            }
        }
    }

    private void inOrderCutOffNodes(Node node, Node previousNode, Set<String> nodeIds, Graph graph) {
        GraphUtils.incrementGoValue(node);
        System.out.println("Node: " + node.getId());
        if (node.getDegree() == 1) {
            if (!nodeIds.contains(node.getId())) {
                final Node secondNode = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(0));
                graph.removeNode(node);
                System.out.println("- Leaf. Removing from graph, going to " + secondNode.getId());
                inOrderCutOffNodes(secondNode, node, nodeIds, graph);
            } else {
                System.out.println("- Leaf to preserve. Going back.");
            }
        } else if (isNodeWithTwoEdgesButNoLeaf(node, previousNode)) {
            Node node1 = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(0));
            Node node2 = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(1));
            System.out.println("- Node. Degree = 2, no leafs. Removing node from graph, making connection between " + node1.getId() + " oraz " + node2.getId());
            graph.addEdge("e" + node1.getId() + "-" + node2.getId(), node1, node2, false);
            graph.removeNode(node);
            if (node1.equals(previousNode)) {
                System.out.println(" - Going to next merged node " + node2.getId());
                inOrderCutOffNodes(node2, node1, nodeIds, graph);
            } else {
                System.out.println(" - Going to next merged node " + node1.getId());
                inOrderCutOffNodes(node1, node2, nodeIds, graph);
            }
        } else {
            for (Edge edge : node.getEdgeSet()) {
                final Node secondNode = GraphUtils.getSecondNodeFromEdge(node, edge);
                if (GraphUtils.getGoValue(secondNode) == 0) {
                    System.out.println("- Node. Going to second node " + secondNode.getId());
                    inOrderCutOffNodes(secondNode, node, nodeIds, graph);
                } else {
                    System.out.println("- Node, already seen.");
                }
            }
        }
    }

    private boolean isNodeWithTwoEdgesButNoLeaf(Node node, Node previousNode) {
        if (node.getDegree() != 2) {
            return false;
        }
        for (Edge edge : node.getEdgeSet()) {
            final Node secondNode = GraphUtils.getSecondNodeFromEdge(node, edge);
            if (GraphUtils.isLeaf(secondNode)) {
                return false;
            }
        }
        return true;
    }

    private void displayInitialGraph(String filename) throws URISyntaxException {
        Graph gDis = GraphUtils.loadGraph(filename);
        gDis.display();
    }

    private Optional<Node> findRoot(Graph g) {
        return g.getNodeSet().stream()
                .filter(n -> n.getDegree() == 2)
                .findAny();
    }
}
