package pl.edu.pg.app.clusters;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeToClustersFamilyConverter {

    private AdjacencyList tree;
    private List<String> leaves;

    public void convert(String treeFilename) {
        tree = loadTreeFromFile(treeFilename);

        Set<Set<String>> clusters = new HashSet<>();
        leaves = tree.adjacentNodesPerNode.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        tree.adjacentNodesPerNode.forEach((keyNode, adjacentNodes) -> {
            //Jeżeli wierzchołek jest liściem, jest on dodawany jako jednoelementowa kolekcja do rodziny
            if (adjacentNodes.isEmpty()) {
                clusters.add(Collections.singleton(keyNode));
            } else {
                //Zbieramy wierzchołki, które są liśćmi
                Set<String> currentNodeLeaves = adjacentNodes.stream()
                        .filter(node -> leaves.contains(node))
                        .collect(Collectors.toSet());

                //Następnie wierzchołki, które nie są liśćmi
                Set<String> notLeafNodes = adjacentNodes.stream()
                        .filter(node -> !leaves.contains(node))
                        .collect(Collectors.toSet());

                //Dla wszystkich tych, które nie są liśćmi, musimy znaleźć ich wszystkie liście
                currentNodeLeaves.addAll(getLeaves(notLeafNodes));

                clusters.add(currentNodeLeaves);
            }
        });

        System.out.println(clusters);
    }

    private Set<String> getLeaves(Set<String> adjacentNodes) {
        Set<String> currentLeaves = new HashSet<>(4);
        adjacentNodes.forEach(adjNode -> {
            if (leaves.contains(adjNode)) {
                currentLeaves.add(adjNode);
            } else {
                //Rekurencyjnie "zbieramy" wszystkie liście dla kolejnych węzłów, które liśćmi nie są.
                currentLeaves.addAll(getLeaves(tree.get(adjNode)));
            }
        });
        return currentLeaves;
    }

    private AdjacencyList loadTreeFromFile(String filename) {
        String path = GraphLoader.getFilePath(filename);
        Graph graph = GraphLoader.load(path);

        GraphToAdjListConverter converter = new GraphToAdjListConverter();
        return converter.convert(graph);
    }

    private Graph loadGraphFromFile(String filename) {
        String path = GraphLoader.getFilePath(filename);
        return GraphLoader.load(path);
    }
}
