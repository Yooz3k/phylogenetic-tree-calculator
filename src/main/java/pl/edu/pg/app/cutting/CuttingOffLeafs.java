package pl.edu.pg.app.cutting;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import pl.edu.pg.app.metric.GraphUtils;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CuttingOffLeafs {

    private Node root;

    public void cut(String filename, List<String> s) {
        Set<String> nodeIds = new HashSet<>(s);

        try {
            displayInitialGraph(filename);
            Graph g = GraphUtils.loadGraph(filename);
            throwExceptionIfIncorrectData(g, nodeIds);

            root = findRoot(g);
            removeLeafs(g, nodeIds);
            removeUnnecesaryNodes(nodeIds, g);

            g.display();
        } catch (URISyntaxException e) {
            System.out.println("Problem ze ścieżką do pliku");
            e.printStackTrace();
        }

    }

    private void removeUnnecesaryNodes(Set<String> nodeIds, Graph g) {
        if (root != null) {
            postOrderCutOffNodes(root, null, nodeIds, g);
        } else {
            postOrderCutOffNodes(g.getNodeSet().stream().filter(n -> !GraphUtils.isLeaf(n)).findFirst().get(), null, nodeIds, g);
        }
        if (g.getNodeSet().size() == 2 && nodeIds.size() == 1) {
            Node nodeToRemove = null;
            for (Node n : g.getNodeSet()) {
                if (!n.getId().equals(nodeIds.iterator().next())) {
                    nodeToRemove = n;
                }
            }
            g.removeNode(nodeToRemove);
        }
    }

    private void removeLeafs(Graph g, Set<String> nodeIds) {
        final Set<Node> nodesToRemove = new HashSet<>();
        for (Node n : g.getNodeSet()) {
            if (GraphUtils.isLeaf(n) && !nodeIds.contains(n.getId())) {
                nodesToRemove.add(n);
            }
        }
        for (Node n : nodesToRemove) {
            g.removeNode(n);
            System.out.println("Removed node " + n.getId());
        }
    }

    private void throwExceptionIfIncorrectData(Graph g, Set<String> nodeIds) {
        for (Node n : g.getNodeSet()) {
            if (nodeIds.contains(n.getId()) && !GraphUtils.isLeaf(n)) {
                throw new IllegalStateException("Not all nodes are leafs!");
            }
        }
    }

    private void postOrderCutOffNodes(Node node, Node previousNode, Set<String> nodeIds, Graph graph) {
        System.out.println("Node: " + node.getId());
        for (Edge e : node.getEdgeSet()) {
            final Node secondNodeFromEdge = GraphUtils.getSecondNodeFromEdge(node, e);
            if (!secondNodeFromEdge.equals(previousNode)) {
                System.out.println("- Node. Going to second node " + secondNodeFromEdge.getId());
                postOrderCutOffNodes(secondNodeFromEdge, node, nodeIds, graph);
            }
        }

        System.out.println(" - Node: " + node.getId());

        if (node.getDegree() == 1) {
            if (!nodeIds.contains(node.getId())) {
                final Node secondNode = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(0));
//                inOrderCutOffNodes(secondNode, node, nodeIds, graph);
                graph.removeNode(node);
                System.out.println("- Leaf. Removing from graph, going to " + secondNode.getId());
            } else {
                System.out.println("- Leaf to preserve. Going back.");
            }
        } else if (isNodeWithTwoEdgesButNoLeaf(node, previousNode)) {
            removeNodeAndMergeHisNeighbours(node, previousNode, nodeIds, graph);
        }
    }

    private void removeNodeAndMergeHisNeighbours(Node node, Node previousNode, Set<String> nodeIds, Graph graph) {
        Node node1 = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(0));
        Node node2 = GraphUtils.getSecondNodeFromEdge(node, node.getEdge(1));
        System.out.println("- Node. Degree = 2, no leafs. Removing node from graph, making connection between " + node1.getId() + " oraz " + node2.getId());
        graph.addEdge("e" + node1.getId() + "-" + node2.getId(), node1, node2, false);
        graph.removeNode(node);
    }

    private boolean isNodeWithTwoEdgesButNoLeaf(Node node, Node previousNode) {
        if (node.getDegree() != 2) {
            return false;
        }
        int leafsFound = 0;
        for (Edge edge : node.getEdgeSet()) {
            final Node secondNode = GraphUtils.getSecondNodeFromEdge(node, edge);
            if (GraphUtils.isLeaf(secondNode)) {
                leafsFound++;
            }
        }
        if (leafsFound > 1) {
            return false;
        }
        if (root != null && root.equals(node)) {
            return false;
        }
        return true;
    }

    private void displayInitialGraph(String filename) throws URISyntaxException {
        Graph gDis = GraphUtils.loadGraph(filename);
        gDis.display();
    }

    private Node findRoot(Graph g) {
        return g.getNodeSet().stream()
                .filter(n -> n.getDegree() == 2)
                .findAny().orElse(null);
    }
}
