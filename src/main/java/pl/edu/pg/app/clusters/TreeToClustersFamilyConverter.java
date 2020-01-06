package pl.edu.pg.app.clusters;

import org.graphstream.graph.Graph;
import pl.edu.pg.app.converter.GraphToAdjListConverter;
import pl.edu.pg.app.io.GraphLoader;
import pl.edu.pg.app.struct.AdjacencyList;

import java.util.*;
import java.util.stream.Collectors;

public class TreeToClustersFamilyConverter {

    private AdjacencyList<String> tree;
    private List<String> leaves;

    public void convert(String treeFilename) {
        tree = loadTreeFromFile(treeFilename);

        Set<Set<String>> clusters = new HashSet<>();
        leaves = tree.GetNodes().stream()
                .filter( node -> tree.GetNodeEdges( node ).isEmpty() )
                .collect(Collectors.toList());

        tree.GetNodes().forEach( keyNode -> {
            List<String> adjacentNodes = tree.GetNodeEdges( keyNode );
            //Jeżeli wierzchołek jest liściem, jest on dodawany jako jednoelementowa kolekcja do rodziny
            if (adjacentNodes.isEmpty()) {
                clusters.add(Collections.singleton(keyNode));
            } else {
                //Zbieramy wierzchołki, które są liśćmi
                List<String> currentNodeLeaves = adjacentNodes.stream()
                        .filter(node -> leaves.contains(node))
                        .collect(Collectors.toList());

                //Następnie wierzchołki, które nie są liśćmi
                List<String> notLeafNodes = adjacentNodes.stream()
                        .filter(node -> !leaves.contains(node))
                        .collect(Collectors.toList());

                //Dla wszystkich tych, które nie są liśćmi, musimy znaleźć ich wszystkie liście
                currentNodeLeaves.addAll(getLeaves(notLeafNodes));

                clusters.add(Set.copyOf( currentNodeLeaves ));
            }
        });

        System.out.println(clusters);
    }

    private Set<String> getLeaves(List<String> adjacentNodes) {
        Set<String> currentLeaves = new HashSet<>(4);
        adjacentNodes.forEach(adjNode -> {
            if (leaves.contains(adjNode)) {
                currentLeaves.add(adjNode);
            } else {
                //Rekurencyjnie "zbieramy" wszystkie liście dla kolejnych węzłów, które liśćmi nie są.
                currentLeaves.addAll(getLeaves(tree.GetNodeEdges(adjNode)));
            }
        });
        return currentLeaves;
    }

    private AdjacencyList<String> loadTreeFromFile(String filename) {
        String path = GraphLoader.getFilePath(filename);
        Graph graph = GraphLoader.load(path);
        //graph.display();

        GraphToAdjListConverter converter = new GraphToAdjListConverter();
        return converter.convert(graph);
    }

    private Graph loadGraphFromFile(String filename) {
        String path = GraphLoader.getFilePath(filename);
        return GraphLoader.load(path);
    }
}
